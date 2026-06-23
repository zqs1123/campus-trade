package org.zqs.orderservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.zqs.orderservice.dto.OrderCreateDTO;
import org.zqs.orderservice.dto.OrderVO;

public interface OrderService {
    String createOrder(Integer buyerId, OrderCreateDTO dto);
    void cancelOrder(Integer orderId, Integer userId);
    void payOrder(Integer orderId, Integer buyerId);
    void shipOrder(Integer orderId, Integer sellerId);       // 新增
    void confirmOrder(Integer orderId, Integer buyerId);     // 新增
    Page<OrderVO> listOrders(Integer userId, Integer page, Integer size, String role);
    OrderVO getOrderDetail(Integer orderId, Integer userId);
    void cancelOrderByTimeout(Integer orderId);
}