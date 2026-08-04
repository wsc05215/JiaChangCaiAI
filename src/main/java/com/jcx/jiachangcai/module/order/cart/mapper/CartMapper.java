package com.jcx.jiachangcai.module.order.cart.mapper;

import com.jcx.jiachangcai.module.order.cart.entity.Cart;
import com.jcx.jiachangcai.module.order.cart.entity.CartVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 购物车表 Mapper 接口
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
public interface CartMapper extends BaseMapper<Cart> {

    List<CartVO> selectCartWithProduct(@Param("userId") Long userId);
}
