package com.jcx.jiachangcai.module.order.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.order.cart.entity.Cart;
import com.jcx.jiachangcai.module.order.cart.entity.CartVO;
import com.jcx.jiachangcai.module.order.cart.mapper.CartMapper;
import com.jcx.jiachangcai.module.order.cart.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    @Autowired
    private CartMapper mapper;

    @Override
    public String addProduct(Long product_id, Long user_id) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, user_id)
               .eq(Cart::getProductId, product_id);
        Cart exist = mapper.selectOne(wrapper);

        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + 1);
            exist.setUpdateTime(LocalDateTime.now());
            mapper.updateById(exist);
            return "ok";
        }

        Cart cart = new Cart();
        cart.setUserId(user_id);
        cart.setProductId(product_id);
        cart.setQuantity(1);
        cart.setSelected(1);
        cart.setIsDeleted(0);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        mapper.insert(cart);
        return "ok";
    }

    @Override
    public boolean isCart(Long product_id, Long user_id) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getProductId, product_id)
               .eq(Cart::getUserId, user_id);
        return mapper.selectOne(wrapper) != null;
    }

    @Override
    public List<CartVO> getCart(Long user_id) {
        return mapper.selectCartWithProduct(user_id);
    }
}
