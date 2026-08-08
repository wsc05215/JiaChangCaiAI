package com.jcx.jiachangcai.module.order.order_item.controller;


import com.jcx.jiachangcai.module.order.order_item.entity.OrderDetailVO;
import com.jcx.jiachangcai.module.order.order_item.entity.OrderItem;
import com.jcx.jiachangcai.module.order.order_item.service.IOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 订单明细表 前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
@RestController
@RequestMapping("/order-item")
public class OrderItemController {

    @Autowired
    private IOrderItemService service;

    @PostMapping("/addOrder")
    public String addOrder(OrderItem orderItem) {
        return service.addOrder(orderItem);
    }

    @GetMapping("/getOrderitem")
    public List<OrderItem> getOrderitem(Long user_id) {
        return service.getOrderitem(user_id);
    }

    @PostMapping("/confirmReceive")
    public String confirmReceive(@RequestParam Long itemId) {
        return service.confirmReceive(itemId);
    }

    @PostMapping("/requestReturn")
    public String requestReturn(@RequestParam Long itemId, @RequestParam String reason) {
        return service.requestReturn(itemId, reason);
    }

    @PostMapping("/requestBatchReturn")
    public String requestBatchReturn(@RequestParam List<Long> itemIds, @RequestParam String reason) {
        return service.requestBatchReturn(itemIds, reason);
    }

    @PostMapping("/cancelReturn")
    public String cancelReturn(@RequestParam Long itemId) {
        return service.cancelReturn(itemId);
    }

    @GetMapping("/getReturnOrders")
    public List<OrderItem> getReturnOrders(Long user_id) {
        return service.getReturnOrders(user_id);
    }

    @GetMapping("/getOrderDetail")
    public OrderDetailVO getOrderDetail(@RequestParam Long orderId) {
        return service.getOrderDetail(orderId);
    }
}