package com.jcx.jiachangcai.module.order.mealplan.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MealPlanVO {
    // 菜单计划字段
    private Long id;
    private Long userId;
    private LocalDate planDate;
    private String mealType;
    private Long recipeId;
    private Integer sortOrder;
    private LocalDateTime createTime;

    // 菜谱字段
    private String recipeTitle;
    private String recipeCoverImages;
    private BigDecimal recipeRating;
    private String recipeDescription;
    private Integer recipeFavoriteCount;
}
