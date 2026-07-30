package com.jcx.jiachangcai.module.search.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.search.mapper.searchMapper;
import com.jcx.jiachangcai.module.search.service.searchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class searchServiceImpl extends ServiceImpl<searchMapper, Recipe>
        implements searchService {

    @Autowired
    private searchMapper mapper;

    @Override
    public List<Recipe> searchByKeyword(String keyword) {
        return mapper.searchByKeyword(keyword);
    }

}
