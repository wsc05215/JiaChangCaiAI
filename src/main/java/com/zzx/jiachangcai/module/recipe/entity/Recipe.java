package com.zzx.jiachangcai.module.recipe.entity;

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
 * 
 * </p>
 *
 * @author wsc
 * @since 2026-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("recipe")
@Schema(description = "Recipe对象")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "recipe_id", type = IdType.AUTO)
    private Long recipeId;

    private String title;

    private String description;

    private String coverImages;

    private BigDecimal rating;

    private Long authorId;

    private String cookTime;

    private String difficulty;

    private String calories;

    private String ingredients;

    private String steps;

    private Integer likeCount;

    private Integer commentCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
