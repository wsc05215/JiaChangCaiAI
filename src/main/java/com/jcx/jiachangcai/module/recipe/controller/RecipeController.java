package com.zzx.jiachangcai.module.recipe.controller;


import com.zzx.jiachangcai.module.recipe.entity.Recipe;
import com.zzx.jiachangcai.module.recipe.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/recipe")
public class RecipeController {
    @Autowired
    private IRecipeService service;
    //推荐页菜谱
    @GetMapping("/getAllRecipe")
    public List<Recipe> getAllRecipe(){
        List<Recipe> AllRecipe = service.getAllRecipe();
        return AllRecipe;
    }

    //关注页菜谱
    @GetMapping("/getAllRecipeOfFollow")
    public List<Recipe> AllRecipeOfFollow(Long id){
        List<Recipe> AllRecipeOfFollow = service.getReciprOfFollow(id);
        return AllRecipeOfFollow;
    }
    //我发布的菜谱查询
    @GetMapping("/ownRecipe")
    public List<Recipe> ownRecipe(Long id){
        List<Recipe> ownRecipe =service.getRecipeOfOwn(id);
        return ownRecipe;
    }


    //取消收藏
    @GetMapping("/decrementLikeCount")
    public Integer decrementLikeCount(Long recipe_id){
       Integer back = service.decrementLikeCount(recipe_id);
       return back;
    }

    //收藏
    @GetMapping("/incrementLikeCount")
    public Integer incrementLikeCount(Long recipe_id){
        Integer back = service.incrementLikeCount(recipe_id);
        return back;
    }
}
