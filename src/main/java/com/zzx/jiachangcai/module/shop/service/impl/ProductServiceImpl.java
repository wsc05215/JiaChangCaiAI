package com.zzx.jiachangcai.module.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzx.jiachangcai.module.shop.entity.Product;
import com.zzx.jiachangcai.module.shop.mapper.ProductMapper;
import com.zzx.jiachangcai.module.shop.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(7).atStartOfDay();
        wrapper.ge(Product::getCreatedAt, start)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreatedAt);
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
