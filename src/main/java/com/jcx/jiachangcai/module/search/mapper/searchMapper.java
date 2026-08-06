package com.jcx.jiachangcai.module.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface searchMapper extends BaseMapper<Recipe> {
    List<Recipe> searchByKeyword(String keyword);
    List<Product> getProductByKeyword(String keyword);
}
