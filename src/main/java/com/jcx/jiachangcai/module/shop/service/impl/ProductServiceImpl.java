package com.jcx.jiachangcai.module.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.shop.entity.Product;
import com.jcx.jiachangcai.module.shop.mapper.ProductMapper;
import com.jcx.jiachangcai.module.shop.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-07-29
 */
@Service
@Primary
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Override
    public List<Product> selectProductOfRecent() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreatedAt)
                .last("LIMIT 10");
        return this.list(wrapper);
    }

    @Override
    public List<Product> getOfSales() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales);
        return this.list(wrapper);
    }

    @Override
    public List<Product> getOfCategory(String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategory, category)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales);
        return this.list(wrapper);
    }

}
