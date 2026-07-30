package com.zzx.jiachangcai.module.recipe.mapper;

import com.zzx.jiachangcai.module.recipe.entity.Recipe;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wsc
 * @since 2026-07-25
 */
public interface RecipeMapper extends BaseMapper<Recipe> {
    Integer seletcountByid(Long id);
    Integer selectLikecount(Long id);
    List<Recipe> selectAllRecipe();
    List<Recipe> selectReciprOfFollow(Long id);
    List<Recipe> selectRecipeOfOwn(Long id);

    Integer incrementLikeCount(Long recipeId);

    Integer decrementLikeCount(Long recipeId);
}
