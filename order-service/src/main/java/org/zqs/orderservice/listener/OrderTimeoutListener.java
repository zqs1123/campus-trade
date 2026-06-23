package org.zqs.orderservice.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zqs.orderservice.service.OrderService;

@Component
public class OrderTimeoutListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "order.dlx.queue")
    public void handleTimeout(String orderId) {
        System.out.println("收到超时消息，订单ID：" + orderId);
        orderService.cancelOrderByTimeout(Integer.parseInt(orderId));
    }
}