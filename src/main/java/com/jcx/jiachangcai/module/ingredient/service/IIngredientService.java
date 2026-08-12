package com.jcx.jiachangcai.module.ingredient.service;

import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

public interface IIngredientService extends IService<Ingredient> {

    void addIngredient(Long user_id, String name, String category, LocalDateTime createTime);

    /**
     * 拍照识别后批量入库（会员门禁 + 补全默认字段）。
     *
     * @param userId 用户ID
     * @param items  已识别、已规范化分类的食材（只含 name/category）
     * @return 实际入库的食材列表；非会员返回空列表
     */
    List<Ingredient> addRecognizedItems(Long userId, List<Ingredient> items);

    java.util.List<Ingredient> listByUserId(Long userId);

    long countByUserId(Long userId);

    long countNearExpiry(Long userId);

    long countExpired(Long userId);

    void deleteIngredient(Long ingredientId);
}
