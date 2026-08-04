package com.jcx.jiachangcai.module.order.cart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 购物车表
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart")
@Schema(description = "购物车表")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车主键")
    @TableId(value = "cart_id", type = IdType.AUTO)
    private Long cartId;

    @Schema(description = "用户ID，关联user表")
    private Long userId;

    @Schema(description = "商品ID，关联product表")
    private Long productId;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "是否勾选 1选中 0未选")
    private Integer selected;

    @Schema(description = "逻辑删除 0正常 1已删")
    private Integer isDeleted;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
