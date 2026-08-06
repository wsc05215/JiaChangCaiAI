package com.jcx.jiachangcai.module.search;

import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.search.service.searchService;
import com.jcx.jiachangcai.module.shop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/search")
public class searchController {

    @Autowired
    private searchService service;

    @GetMapping(produces = "application/json;charset=UTF-8")
    public List<Recipe> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return service.searchByKeyword(keyword.trim());
    }



    @GetMapping("/searchProduct")
    public List<Product> searchProduct(String keyword){
       List<Product> product = service.searchProductByKeyword(keyword);
       return product;
    }

}
