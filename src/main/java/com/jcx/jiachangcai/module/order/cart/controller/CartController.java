package com.jcx.jiachangcai.module.order.cart.controller;


import com.jcx.jiachangcai.module.order.cart.entity.CartVO;
import com.jcx.jiachangcai.module.order.cart.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService service;

    @PostMapping("/addCart")
    public String addCart(Long product_id, Long user_id) {
        return service.addProduct(product_id, user_id);
    }

    @GetMapping("/getCart")
    public List<CartVO> getCart(Long user_id) {
        return service.getCart(user_id);
    }
}
