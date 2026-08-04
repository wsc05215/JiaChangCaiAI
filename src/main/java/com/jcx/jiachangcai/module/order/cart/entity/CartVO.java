package com.jcx.jiachangcai.module.order.cart.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartVO {
    // 购物车字段
    private Long cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer selected;

    // 商品字段
    private String productName;
    private BigDecimal productPrice;
    private String productImage;
    private String productUnit;
}
