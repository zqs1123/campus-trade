package org.zqs.orderservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zqs.common.entity.Result;
import org.zqs.orderservice.dto.OrderCreateDTO;
import org.zqs.orderservice.dto.OrderVO;
import org.zqs.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody OrderCreateDTO dto,
                                 @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        String orderNo = orderService.createOrder(userId, dto);
        return Result.success(orderNo);
    }

    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Integer id,
                                 @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        orderService.cancelOrder(id, userId);
        return Result.success("取消成功");
    }

    @PutMapping("/{id}/pay")
    public Result<String> pay(@PathVariable Integer id,
                              @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        orderService.payOrder(id, userId);
        return Result.success("支付成功");
    }

    // ========== 新增发货接口 ==========
    @PutMapping("/{id}/ship")
    public Result<String> ship(@PathVariable Integer id,
                               @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        orderService.shipOrder(id, userId);
        return Result.success("发货成功");
    }

    // ========== 新增确认收货接口 ==========
    @PutMapping("/{id}/confirm")
    public Result<String> confirm(@PathVariable Integer id,
                                  @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        orderService.confirmOrder(id, userId);
        return Result.success("确认收货成功");
    }

    @GetMapping("/list")
    public Result<Page<OrderVO>> list(@RequestParam Integer page,
                                      @RequestParam Integer size,
                                      @RequestParam(defaultValue = "buyer") String role,
                                      @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        Page<OrderVO> result = orderService.listOrders(userId, page, size, role);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Integer id,
                                  @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        OrderVO vo = orderService.getOrderDetail(id, userId);
        return Result.success(vo);
    }
}