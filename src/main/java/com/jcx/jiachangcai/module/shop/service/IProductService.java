package com.jcx.jiachangcai.module.shop.service;

import com.jcx.jiachangcai.module.shop.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author wsc
 * @since 2026-07-29
 */
public interface IProductService extends IService<Product> {
    List<Product> selectProductOfRecent();
    List<Product> getOfSales();
    List<Product> getOfCategory(String category);
}
