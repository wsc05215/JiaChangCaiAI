package com.jcx.jiachangcai.module.ingredient.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ingredient")
@Schema(description = "用户食材表")
public class Ingredient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "食材主键")
    @TableId(value = "ingredient_id", type = IdType.AUTO)
    private Long ingredientId;

    @Schema(description = "所属用户ID")
    private Long userId;

    @Schema(description = "食材名称")
    private String name;

    @Schema(description = "食材分类：蔬菜/生禽/蛋类/水产/豆制品/其他")
    private String category;

    @Schema(description = "存入时间")
    private LocalDateTime createTime;

    @Schema(description = "储存方式：冷藏/冷冻/常温")
    private String storageMethod;

    @Schema(description = "购买日期")
    private LocalDate purchaseDate;

    @Schema(description = "保质天数（AI判定）")
    private Integer expireDays;

    @Schema(description = "过期日期（purchaseDate + expireDays）")
    private LocalDateTime expireDate;

}
