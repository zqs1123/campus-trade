package org.zqs.campustrade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zqs.campustrade.dto.OrderCreateDTO;
import org.zqs.campustrade.dto.OrderVO;
import org.zqs.campustrade.service.OrderService;
import org.zqs.campustrade.utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public String create(@RequestBody OrderCreateDTO dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) return "请先登录";
        if (token.startsWith("Bearer ")) token = token.substring(7);
        Integer userId = JwtUtil.getUserID(token);
        return orderService.createOrder(userId, dto);
    }

    // 新增付款接口
    @PutMapping("/pay/{orderId}")
    public String pay(@PathVariable Integer orderId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) return "请先登录";
        if (token.startsWith("Bearer ")) token = token.substring(7);
        Integer userId = JwtUtil.getUserID(token);
        orderService.payOrder(orderId, userId);
        return "付款成功";
    }

    @PutMapping("/ship/{orderId}")
    public String ship(@PathVariable Integer orderId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) return "请先登录";
        if (token.startsWith("Bearer ")) token = token.substring(7);
        Integer userId = JwtUtil.getUserID(token);
        orderService.shipOrder(orderId, userId);
        return "发货成功";
    }

    @PutMapping("/confirm/{orderId}")
    public String confirm(@PathVariable Integer orderId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) return "请先登录";
        if (token.startsWith("Bearer ")) token = token.substring(7);
        Integer userId = JwtUtil.getUserID(token);
        orderService.confirmOrder(orderId, userId);
        return "确认收货成功";
    }

    @PutMapping("/cancel/{orderId}")
    public String cancel(@PathVariable Integer orderId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) return "请先登录";
        if (token.startsWith("Bearer ")) token = token.substring(7);
        Integer userId = JwtUtil.getUserID(token);
        orderService.cancelOrder(orderId, userId);
        return "订单已取消";
    }

    @GetMapping("/list")
    public Page<OrderVO> list(@RequestParam Integer page,
                              @RequestParam Integer size,
                              @RequestParam String role,
                              HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) throw new RuntimeException("请先登录");
        if (token.startsWith("Bearer ")) token = token.substring(7);
        Integer userId = JwtUtil.getUserID(token);
        return orderService.listOrders(userId, page, size, role);
    }
}