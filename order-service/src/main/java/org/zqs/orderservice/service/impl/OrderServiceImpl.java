package org.zqs.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zqs.api.client.ProductFeignClient;
import org.zqs.common.entity.OrderStatus;
import org.zqs.common.entity.Result;
import org.zqs.common.exception.BusinessException;
import org.zqs.orderservice.dto.OrderCreateDTO;
import org.zqs.orderservice.dto.OrderVO;
import org.zqs.orderservice.entity.Order;
import org.zqs.orderservice.mapper.OrderMapper;
import org.zqs.orderservice.service.OrderService;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public String createOrder(Integer buyerId, OrderCreateDTO dto) {
        // 1. 查商品（Feign）
        Result<?> productResult = productFeignClient.getProductById(dto.getProductId());
        if (productResult.getCode() != 200 || productResult.getData() == null) {
            throw new BusinessException("商品不存在");
        }
        // 2. 扣库存（Feign）
        Result<?> stockResult = productFeignClient.deductStock(dto.getProductId(), 1);
        if (stockResult.getCode() != 200) {
            throw new BusinessException("库存不足");
        }
        // 3. 创建订单
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        order.setProductId(dto.getProductId());
        order.setBuyerId(buyerId);
        // sellerId 从商品信息获取，暂写死为1（实际应从productResult中解析）
        order.setSellerId(1);
        order.setAmount(dto.getAmount());
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        // 4. 发送延迟消息（30秒后超时取消，测试用）
        rabbitTemplate.convertAndSend(
                "order.delay.exchange",
                "order.delay.routing",
                String.valueOf(order.getId()),
                message -> {
                    message.getMessageProperties().setExpiration(String.valueOf(30 * 60 * 1000)); // 30秒
                    return message;
                }
        );

        return order.getOrderNo();
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, Integer userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException("当前订单状态不可取消");
        }
        order.setStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(order);
        // 回滚库存
        productFeignClient.deductStock(order.getProductId(), -order.getQuantity());
    }

    @Override
    @Transactional
    public void payOrder(Integer orderId, Integer buyerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException("无权限操作");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException("当前订单状态不可支付");
        }
        order.setStatus(OrderStatus.PAID.getCode());
        orderMapper.updateById(order);
    }

    @Override
    public Page<OrderVO> listOrders(Integer userId, Integer page, Integer size, String role) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if ("buyer".equals(role)) {
            wrapper.eq("buyer_id", userId);
        } else if ("seller".equals(role)) {
            wrapper.eq("seller_id", userId);
        } else {
            throw new BusinessException("角色参数错误");
        }
        wrapper.orderByDesc("create_time");

        Page<Order> pageParam = new Page<>(page, size);
        orderMapper.selectPage(pageParam, wrapper);

        Page<OrderVO> voPage = new Page<>(pageParam.getCurrent(), pageParam.getSize(), pageParam.getTotal());
        voPage.setRecords(pageParam.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setStatusDesc(OrderStatus.of(order.getStatus()).getDesc());
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public OrderVO getOrderDetail(Integer orderId, Integer userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权限查看");
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusDesc(OrderStatus.of(order.getStatus()).getDesc());
        return vo;
    }

    @Override
    @Transactional
    public void cancelOrderByTimeout(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return;
        }
        // 只有待付款状态才处理
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            return;
        }

        // ⭐ 先回滚库存（关键修复）
        try {
            productFeignClient.deductStock(order.getProductId(), -order.getQuantity());
        } catch (Exception e) {
            // 如果回滚失败，记录日志或发送告警，但订单状态仍需变更
            System.err.println("库存回滚失败，订单ID：" + orderId + "，错误：" + e.getMessage());
            // 这里可以加告警逻辑，但不阻断订单取消
        }

        // 再更新订单状态
        order.setStatus(OrderStatus.TIMEOUT.getCode());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void shipOrder(Integer orderId, Integer sellerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException("无权限操作此订单");
        }
        if (order.getStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException("当前订单状态不可发货");
        }
        order.setStatus(OrderStatus.SHIPPED.getCode());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void confirmOrder(Integer orderId, Integer buyerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException("无权限操作此订单");
        }
        if (order.getStatus() != OrderStatus.SHIPPED.getCode()) {
            throw new BusinessException("当前订单状态不可确认收货");
        }
        order.setStatus(OrderStatus.COMPLETED.getCode());
        orderMapper.updateById(order);
    }
}