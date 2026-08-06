package com.jcx.jiachangcai.module.ingredient.service.impl;

import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.jcx.jiachangcai.module.ingredient.mapper.IngredientMapper;
import com.jcx.jiachangcai.module.ingredient.service.IIngredientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IngredientServiceImpl extends ServiceImpl<IngredientMapper, Ingredient> implements IIngredientService {
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
            mapper.insert(ingredient);
        }
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
        java.util.List<Ingredient> list = listByUserId(userId);
        return list.stream().filter(Ingredient::isNearExpiry).count();
    }

    @Override
    public void deleteIngredient(Long ingredientId) {
        mapper.deleteById(ingredientId);
    }
}
