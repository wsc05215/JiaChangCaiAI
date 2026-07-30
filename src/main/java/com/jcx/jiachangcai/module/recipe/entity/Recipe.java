package com.jcx.jiachangcai.module.recipe.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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

    @Schema(description = "食谱ID")
    @TableId(value = "recipe_id", type = IdType.AUTO)
    private Long recipeId;

    @Schema(description = "食谱标题")
    private String title;

    @Schema(description = "食谱简介")
    private String description;

    @Schema(description = "封面图片 (JSON数组)")
    private String coverImages;

    @Schema(description = "评分 (0.0~5.0)")
    private BigDecimal rating;

    @Schema(description = "作者用户ID")
    private Long authorId;

    @Schema(description = "烹饪时长")
    private String cookTime;

    @Schema(description = "难度等级")
    private String difficulty;

    @Schema(description = "卡路里")
    private String calories;

    @Schema(description = "食材清单 (JSON数组)")
    private String ingredients;

    @Schema(description = "烹饪步骤 (JSON数组)")
    private String steps;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "收藏数")
    @TableField(exist = false)
    private Integer favoriteCount;

    @Schema(description = "状态: 0=草稿, 1=已发布, 2=已下架")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "作者昵称")
    @TableField(exist = false)
    private String authorName;

    @Schema(description = "作者头像")
    @TableField(exist = false)
    private String authorAvatar;

}
