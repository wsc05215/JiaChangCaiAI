package com.jcx.jiachangcai.module.order.order_item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.order.order_item.entity.OrderItem;
import com.jcx.jiachangcai.module.order.order_item.mapper.OrderItemMapper;
import com.jcx.jiachangcai.module.order.order_item.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcx.jiachangcai.module.shop.entity.Product;
import com.jcx.jiachangcai.module.shop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

    @Autowired
    private OrderItemMapper mapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addOrder(OrderItem orderItem) {
        // 查库存
        Product product = productMapper.selectById(orderItem.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        int remain = product.getStock() - orderItem.getQuantity();
        if (remain < 0) {
            throw new RuntimeException("库存不足");
        }
        // 扣库存
        product.setStock(remain);
        productMapper.updateById(product);
        // 写订单明细
        orderItem.setCreateTime(LocalDateTime.now());
        mapper.insert(orderItem);
        return "下单成功";
    }

    @Override
    public List<OrderItem> getOrderitem(Long userId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getUserId, userId)
               .eq(OrderItem::getIsDeleted, 0)
               .orderByDesc(OrderItem::getCreateTime);
        return mapper.selectList(wrapper);
    }
}
