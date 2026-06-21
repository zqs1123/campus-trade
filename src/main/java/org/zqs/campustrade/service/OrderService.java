package org.zqs.campustrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.zqs.campustrade.dto.OrderCreateDTO;
import org.zqs.campustrade.dto.OrderVO;

public interface OrderService {
    String createOrder(Integer buyerId, OrderCreateDTO dto);
    void payOrder(Integer orderId, Integer buyerId);          // 新增付款
    void shipOrder(Integer orderId, Integer sellerId);
    void confirmOrder(Integer orderId, Integer buyerId);
    void cancelOrder(Integer orderId, Integer userId);
    Page<OrderVO> listOrders(Integer userId, Integer page, Integer size, String role);
}