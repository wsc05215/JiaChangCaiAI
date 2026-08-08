package com.jcx.jiachangcai.module.ingredient.service;

import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;

public interface IIngredientService extends IService<Ingredient> {

    void addIngredient(Long user_id, String name, String category, LocalDateTime createTime);

    java.util.List<Ingredient> listByUserId(Long userId);

    long countByUserId(Long userId);

    long countNearExpiry(Long userId);

    long countExpired(Long userId);

    void deleteIngredient(Long ingredientId);
}
