package com.jcx.jiachangcai.module.order.order_item.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "订单详情VO（含发货地址）")
public class OrderDetailVO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "发货地址（取自商品）")
    private String deliveryAddress;

    @Schema(description = "订单创建时间")
    private LocalDateTime createTime;

    @Schema(description = "订单合计金额")
    private BigDecimal total;

    @Schema(description = "订单商品明细")
    private List<OrderItem> items;
}
