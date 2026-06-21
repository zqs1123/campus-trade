package org.zqs.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zqs.campustrade.dto.OrderCreateDTO;
import org.zqs.campustrade.dto.OrderVO;
import org.zqs.campustrade.entity.Order;
import org.zqs.campustrade.entity.Product;
import org.zqs.campustrade.entity.User;
import org.zqs.campustrade.enums.OrderStatusEnum;
import org.zqs.campustrade.mapper.OrderMapper;
import org.zqs.campustrade.mapper.ProductMapper;
import org.zqs.campustrade.mapper.UserMapper;
import org.zqs.campustrade.service.OrderService;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public String createOrder(Integer buyerId, OrderCreateDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            return "商品不存在";
        }
        if (product.getStatus() != 0) {
            return "商品已下架或已售出";
        }
        if (product.getUserId().equals(buyerId)) {
            return "不能购买自己发布的商品";
        }

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        order.setProductId(product.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getUserId());
        order.setAmount(product.getPrice());
        order.setStatus(OrderStatusEnum.WAIT_PAY.getCode());  // 待付款
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        // 锁定商品
        product.setStatus(1);
        productMapper.updateById(product);

        return "下单成功，订单号：" + order.getOrderNo();
    }

    @Override
    @Transactional
    public void payOrder(Integer orderId, Integer buyerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (order.getStatus() != OrderStatusEnum.WAIT_PAY.getCode()) {
            throw new RuntimeException("当前订单状态不可付款");
        }
        order.setStatus(OrderStatusEnum.PAID.getCode());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void shipOrder(Integer orderId, Integer sellerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (order.getStatus() != OrderStatusEnum.PAID.getCode()) {
            throw new RuntimeException("当前订单状态不可发货");
        }
        order.setStatus(OrderStatusEnum.SHIPPED.getCode());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void confirmOrder(Integer orderId, Integer buyerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (order.getStatus() != OrderStatusEnum.SHIPPED.getCode()) {
            throw new RuntimeException("当前订单状态不可确认收货");
        }
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, Integer userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (order.getStatus() != OrderStatusEnum.WAIT_PAY.getCode()) {
            throw new RuntimeException("当前订单状态不可取消");
        }
        // 恢复商品状态
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            product.setStatus(0);
            productMapper.updateById(product);
        }
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
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
            throw new RuntimeException("请指定角色 buyer 或 seller");
        }
        wrapper.orderByDesc("create_time");

        Page<Order> orderPage = new Page<>(page, size);
        orderMapper.selectPage(orderPage, wrapper);

        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        voPage.setRecords(orderPage.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setStatusDesc(OrderStatusEnum.of(order.getStatus()).getDesc());

            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                vo.setProductTitle(product.getTitle());
                if (product.getImageUrls() != null) {
                    vo.setProductImage(product.getImageUrls().split(",")[0]);
                }
            }
            User buyer = userMapper.selectById(order.getBuyerId());
            if (buyer != null) {
                vo.setBuyerNickname(buyer.getNickname());
            }
            User seller = userMapper.selectById(order.getSellerId());
            if (seller != null) {
                vo.setSellerNickname(seller.getNickname());
            }
            return vo;
        }).collect(Collectors.toList()));

        return voPage;
    }
}