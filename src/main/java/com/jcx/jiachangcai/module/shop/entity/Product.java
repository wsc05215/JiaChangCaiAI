package com.jcx.jiachangcai.module.shop.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 商品表
 * </p>
 *
 * @author wsc
 * @since 2026-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product")
@Schema(description = "商品表")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题/简介")
    private String subtitle;

    @Schema(description = "商品详情（详细介绍、产地、规格等信息）")
    private String detail;

    @Schema(description = "收获地址（发货地/产地地址）")
    private String deliveryAddress;

    @Schema(description = "售价（最低起售价）")
    private BigDecimal price;

    @Schema(description = "原价（划线价）")
    private BigDecimal originPrice;

    @Schema(description = "主图/封面图URL")
    private String coverImage;

    @Schema(description = "轮播图集，多个URL用逗号分隔")
    private String images;

    @Schema(description = "分类（果蔬/肉蛋/海鲜/速食等）")
    private String category;

    @Schema(description = "标签，多个用逗号分隔（轻食/无菌/有机等）")
    private String tags;

    @Schema(description = "状态：1-上架 2-下架 3-售罄")
    private Integer status;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "单位（斤/盒/段/个）")
    private String unit;

    @Schema(description = "排序权重")
    private Integer sortWeight;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;


}
