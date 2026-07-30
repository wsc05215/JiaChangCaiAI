package com.jcx.jiachangcai.module.social.favorite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.recipe.mapper.RecipeMapper;
import com.jcx.jiachangcai.module.social.favorite.entity.Favorite;
import com.jcx.jiachangcai.module.social.favorite.mapper.FavoriteMapper;
import com.jcx.jiachangcai.module.social.favorite.service.IFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

    @Autowired
    private RecipeMapper recipeMapper;

    @Override
    public boolean addFavorite(Long userId, Long recipeId) {
        if (isFavorited(userId, recipeId)) {
            return false;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setRecipeId(recipeId);
        if (save(favorite)) {
            recipeMapper.incrementLikeCount(recipeId);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFavorite(Long userId, Long recipeId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getRecipeId, recipeId);
        if (remove(wrapper)) {
            recipeMapper.decrementLikeCount(recipeId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFavorited(Long userId, Long recipeId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getRecipeId, recipeId);
        return count(wrapper) > 0;
    }

    @Override
    public List<Long> getFavoriteRecipeIds(Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt);
        return list(wrapper).stream()
                .map(Favorite::getRecipeId)
                .collect(Collectors.toList());
    }

}
