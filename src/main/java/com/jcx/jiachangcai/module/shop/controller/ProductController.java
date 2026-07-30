package com.jcx.jiachangcai.module.shop.controller;


import com.jcx.jiachangcai.module.shop.entity.Product;
import com.jcx.jiachangcai.module.shop.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-07-29
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService service;
    //查询最近上新
     @GetMapping("/getRecent")
    public List<Product> getRecent(){
      List<Product> productOfRecent =  service.selectProductOfRecent();
      return productOfRecent;
     }

    //查看最近七天销量
    @GetMapping("/getProductOfSales")
    public List<Product> getProductOfSales(){
         List<Product> productsOfSales = service.getOfSales();
         return productsOfSales;
    }
    //按属性分类
    @GetMapping("/getProductOfCategory")
    public List<Product> getProductOfCategory(String category){
         List<Product> productOfCategory = service.getOfCategory(category);
         return productOfCategory;
    }

}
