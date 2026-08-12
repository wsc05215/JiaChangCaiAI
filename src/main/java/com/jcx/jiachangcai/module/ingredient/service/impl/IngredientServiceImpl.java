package com.jcx.jiachangcai.module.ingredient.service.impl;

import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.jcx.jiachangcai.module.ingredient.mapper.IngredientMapper;
import com.jcx.jiachangcai.module.ingredient.service.IIngredientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IngredientServiceImpl extends ServiceImpl<IngredientMapper, Ingredient> implements IIngredientService {

    /** 保质期规则表兜底（与 AiPrompts 中 AI 食材管家提示词保持一致），仅在识别模型未给出 expireDays 时使用 */
    private static final java.util.Map<String, Integer> EXPIRE_RULES = java.util.Map.ofEntries(
            java.util.Map.entry("蔬菜|冷藏", 7), java.util.Map.entry("蔬菜|常温", 3), java.util.Map.entry("蔬菜|冷冻", 30),
            java.util.Map.entry("生禽|冷藏", 3), java.util.Map.entry("生禽|冷冻", 90),
            java.util.Map.entry("蛋类|冷藏", 30), java.util.Map.entry("蛋类|常温", 15),
            java.util.Map.entry("水产|冷藏", 2), java.util.Map.entry("水产|冷冻", 60),
            java.util.Map.entry("豆制品|冷藏", 5), java.util.Map.entry("豆制品|冷冻", 30),
            java.util.Map.entry("其他|冷藏", 7), java.util.Map.entry("其他|常温", 7), java.util.Map.entry("其他|冷冻", 30)
    );

    /** 按分类+储存方式查规则表；表外组合回退该分类"冷藏"天数，兜底 7 天 */
    private static int defaultExpireDays(String category, String storageMethod) {
        String cat = (category == null || category.isBlank()) ? "其他" : category;
        String sm = (storageMethod == null || storageMethod.isBlank()) ? "冷藏" : storageMethod;
        Integer days = EXPIRE_RULES.get(cat + "|" + sm);
        if (days != null) return days;
        Integer fallback = EXPIRE_RULES.get(cat + "|冷藏");
        return fallback != null ? fallback : 7;
    }

    /** 补全保质期：AI 已判断的 expireDays 优先保留，没有则用规则表兜底，最后算 expireDate */
    private static void fillDefaultExpiry(Ingredient item) {
        if (item.getExpireDays() == null) {
            item.setExpireDays(defaultExpireDays(item.getCategory(), item.getStorageMethod()));
        }
        if (item.getExpireDate() == null && item.getPurchaseDate() != null) {
            item.setExpireDate(item.getPurchaseDate().plusDays(item.getExpireDays()).atStartOfDay());
        }
    }

    //存入食材
    @Autowired
    private IMemberService service;
    @Autowired
    private IngredientMapper mapper;
    @Override
    public void addIngredient(Long user_id, String name, String category, LocalDateTime createTime) {
        if(service.getisMember(user_id) == true){
            Ingredient ingredient =new Ingredient();
            ingredient.setName(name);
            ingredient.setCategory(category);
            ingredient.setUserId(user_id);
            ingredient.setCreateTime(createTime);
            ingredient.setStorageMethod("冷藏");
            if (createTime != null) ingredient.setPurchaseDate(createTime.toLocalDate());
            fillDefaultExpiry(ingredient);
            mapper.insert(ingredient);
        }
    }

    @Override
    public List<Ingredient> addRecognizedItems(Long userId, List<Ingredient> items) {
        if (!service.getisMember(userId) || items == null || items.isEmpty()) {
            return List.of();
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        List<Ingredient> saved = new ArrayList<>(items.size());
        for (Ingredient item : items) {
            item.setUserId(userId);
            item.setCreateTime(now);
            // 购买日期：前端确认时可指定，未指定则默认今天
            if (item.getPurchaseDate() == null) {
                item.setPurchaseDate(today);
            }
            if (item.getStorageMethod() == null || item.getStorageMethod().isBlank()) {
                item.setStorageMethod("冷藏");
            }
            // 保质期：AI 判断值优先，缺失用规则表兜底
            fillDefaultExpiry(item);
            saved.add(item);
        }
        saveBatch(saved);
        return saved;
    }

    @Override
    public java.util.List<Ingredient> listByUserId(Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Ingredient> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Ingredient::getUserId, userId)
                .orderByDesc(Ingredient::getCreateTime);
        return mapper.selectList(wrapper);
    }

    @Override
    public long countByUserId(Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Ingredient> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Ingredient::getUserId, userId);
        return mapper.selectCount(wrapper);
    }

    @Override
    public long countNearExpiry(Long userId) {
        java.time.LocalDate today = java.time.LocalDate.now();
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Ingredient> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Ingredient::getUserId, userId)
               .isNotNull(Ingredient::getExpireDate)
               .apply("DATE(expire_date) >= {0}", today)
               .apply("DATE(expire_date) <= {0}", today.plusDays(1));
        return mapper.selectCount(wrapper);
    }

    @Override
    public long countExpired(Long userId) {
        java.time.LocalDate today = java.time.LocalDate.now();
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Ingredient> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Ingredient::getUserId, userId)
               .isNotNull(Ingredient::getExpireDate)
               .apply("DATE(expire_date) < {0}", today);
        return mapper.selectCount(wrapper);
    }

    @Override
    public void deleteIngredient(Long ingredientId) {
        mapper.deleteById(ingredientId);
    }
}
