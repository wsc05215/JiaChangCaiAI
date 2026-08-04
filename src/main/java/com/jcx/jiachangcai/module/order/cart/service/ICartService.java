package com.jcx.jiachangcai.module.order.cart.service;

import com.jcx.jiachangcai.module.order.cart.entity.Cart;
import com.jcx.jiachangcai.module.order.cart.entity.CartVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
public interface ICartService extends IService<Cart> {
    String addProduct(Long product_id, Long user_id);
    boolean isCart(Long product_id, Long user_id);
    List<CartVO> getCart(Long user_id);
}
