package com.jcx.jiachangcai.module.ingredient.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    /**
     * 获取保质天数
     */
    public int getExpiryDays() {
        if (category == null) return 30;
        return switch (category) {
            case "蔬菜" -> 7;
            case "生禽", "蛋类" -> 15;
            case "水产" -> 5;
            case "豆制品" -> 7;
            default -> 30;
        };
    }

    /**
     * 距过期还剩几天（正数=未过期，0=今天到期，负数=已过期N天）
     */
    public long getDaysUntilExpiry() {
        int days = getExpiryDays();
        if (days < 0 || createTime == null) return Long.MAX_VALUE;
        long passed = ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
        return days - passed;
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        long remaining = getDaysUntilExpiry();
        return remaining < 0 && remaining > Long.MIN_VALUE;
    }

    /**
     * 是否临期（还剩1天内到期）
     */
    public boolean isNearExpiry() {
        long remaining = getDaysUntilExpiry();
        return remaining >= 0 && remaining <= 1;
    }

}
