package com.zzx.jiachangcai.module.social.favorite.service;

import com.zzx.jiachangcai.module.social.favorite.entity.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IFavoriteService extends IService<Favorite> {

    boolean addFavorite(Long userId, Long recipeId);

    boolean removeFavorite(Long userId, Long recipeId);

    boolean isFavorited(Long userId, Long recipeId);

    List<Long> getFavoriteRecipeIds(Long userId);

}
