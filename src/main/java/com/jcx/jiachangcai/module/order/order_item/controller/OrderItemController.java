package com.jcx.jiachangcai.module.order.order_item.controller;


import com.jcx.jiachangcai.module.order.order_item.entity.OrderItem;
import com.jcx.jiachangcai.module.order.order_item.service.IOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
