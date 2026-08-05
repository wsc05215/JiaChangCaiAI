package com.jcx.jiachangcai.module.order.order_item.entity;

import java.math.BigDecimal;
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
 * 订单明细表
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_item")
@Schema(description = "订单明细表")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "明细主键")
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    @Schema(description = "订单ID，关联order_info表")
    private Long orderId;

    @Schema(description = "用户ID，关联user表")
    private Long userId;

    @Schema(description = "商品ID，关联product表id")
    private Long productId;

    @Schema(description = "商品名称（下单快照）")
    private String productName;

    @Schema(description = "商品封面图（下单快照）")
    private String productImage;

    @Schema(description = "下单时单价")
    private BigDecimal price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "小计（price × quantity）")
    private BigDecimal totalPrice;

    @Schema(description = "逻辑删除：0正常 1已删")
    private Integer isDeleted;

    @Schema(description = "收货时间（用户确认收货的时间）")
    private LocalDateTime receivedTime;

    @Schema(description = "退货状态：0-未退货 1-退货中 2-已退货")
    private Integer returnStatus;

    @Schema(description = "退货原因")
    private String returnReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
