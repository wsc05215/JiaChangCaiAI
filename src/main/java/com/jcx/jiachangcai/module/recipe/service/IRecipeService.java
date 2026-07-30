package com.zzx.jiachangcai.module.recipe.service;

import com.zzx.jiachangcai.module.recipe.entity.Recipe;
import com.zzx.jiachangcai.module.recipe.service.IRecipeService;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wsc
 * @since 2026-07-25
 */
public interface IRecipeService extends IService<Recipe> {
    Integer getcountByid(Long id);
    Integer getLikecount(Long id);
    List<Recipe> getAllRecipe();
    List<Recipe> getReciprOfFollow(Long id);
    List<Recipe> getRecipeOfOwn(Long id);
    Integer incrementLikeCount(Long recipeId);
    Integer decrementLikeCount(Long recipeId);
}
