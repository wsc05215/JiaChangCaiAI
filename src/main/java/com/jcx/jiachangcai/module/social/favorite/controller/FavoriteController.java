package com.jcx.jiachangcai.module.social.favorite.controller;

import com.jcx.jiachangcai.module.social.favorite.service.IFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private IFavoriteService service;

    @PostMapping("/add")
    public boolean addFavorite(Long userId, Long recipeId) {
        return service.addFavorite(userId, recipeId);
    }

    @DeleteMapping("/remove")
    public boolean removeFavorite(Long userId, Long recipeId) {
        return service.removeFavorite(userId, recipeId);
    }

    @GetMapping("/check")
    public boolean isFavorited(Long userId, Long recipeId) {
        return service.isFavorited(userId, recipeId);
    }

    @GetMapping("/list")
    public List<Long> getFavoriteRecipeIds(Long userId) {
        return service.getFavoriteRecipeIds(userId);
    }

}
