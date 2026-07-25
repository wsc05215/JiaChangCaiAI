package com.zzx.jiachangcai.module.recipe.service.impl;

import com.zzx.jiachangcai.module.recipe.entity.Recipe;
import com.zzx.jiachangcai.module.recipe.service.impl.RecipeServiceImpl;
import com.zzx.jiachangcai.module.recipe.mapper.RecipeMapper;
import com.zzx.jiachangcai.module.recipe.service.IRecipeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-07-25
 */
@Service
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
}
