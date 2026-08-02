package com.jcx.jiachangcai.module.recipe.service.impl;

import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.recipe.mapper.RecipeMapper;
import com.jcx.jiachangcai.module.recipe.service.IRecipeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-07-25
 */
@Service
@Primary
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements IRecipeService {
    @Autowired
    private RecipeMapper mapper;
    @Override
    public Integer getcountByid(Long id) {
       Integer count = mapper.seletcountByid(id);
       return count;
    }

    @Override
    public Integer getLikecount(Long id) {
        Integer likecount = mapper.selectLikecount(id);
        return likecount;
    }

    @Override
    public List<Recipe> getAllRecipe() {
        List<Recipe> AllRecipe = mapper.selectAllRecipe();
        //按照评分从大到小排列
        Collections.sort(AllRecipe, Comparator.comparing(Recipe::getLikeCount).reversed());
        return AllRecipe;
    }
    //关注的人的菜谱
    @Override
    public List<Recipe> getReciprOfFollow(Long id) {
        List<Recipe> RecipeOfFollow =mapper.selectReciprOfFollow(id);
        return RecipeOfFollow;
    }
    //我的菜谱查询
    @Override
    public List<Recipe> getRecipeOfOwn(Long id) {
      List<Recipe> RecipeOfOwn =mapper.selectRecipeOfOwn(id);
      return RecipeOfOwn;
    }

    //收藏
    @Override
    public Integer incrementLikeCount(Long recipeId) {
      Integer a =  mapper.decrementLikeCount(recipeId);
       return a;
    }
    //取消收藏
    @Override
    public Integer decrementLikeCount(Long recipeId) {
           Integer a = mapper.decrementLikeCount(recipeId);
           return a;
    }

    @Override
    public Recipe createRecipe(Recipe recipe) {
        recipe.setStatus(1);
        recipe.setRating(java.math.BigDecimal.ZERO);
        recipe.setLikeCount(0);
        recipe.setCommentCount(0);
        recipe.setCreateTime(java.time.LocalDateTime.now());
        recipe.setUpdateTime(java.time.LocalDateTime.now());
        mapper.insert(recipe);
        return recipe;
    }

}
