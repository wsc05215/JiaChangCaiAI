package com.jcx.jiachangcai.module.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.shop.entity.Product;

import java.util.List;

public interface searchService extends IService<Recipe> {
    List<Recipe> searchByKeyword(String keyword);
    List<Product> searchProductByKeyword(String keyword);
}
