package com.jcx.jiachangcai.module.order.order_item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import java.time.temporal.ChronoUnit;
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
        orderItem.setReturnStatus(0);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String confirmReceive(Long itemId) {
        OrderItem item = mapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("订单不存在");
        }
        if (item.getReceivedTime() != null) {
            throw new RuntimeException("该订单已确认收货");
        }
        // 设置收货时间
        LambdaUpdateWrapper<OrderItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OrderItem::getItemId, itemId)
               .set(OrderItem::getReceivedTime, LocalDateTime.now());
        mapper.update(null, wrapper);
        return "确认收货成功";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String requestReturn(Long itemId, String reason) {
        OrderItem item = mapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("订单不存在");
        }
        // 未收货不能退货
        if (item.getReceivedTime() == null) {
            throw new RuntimeException("请先确认收货后再申请退货");
        }
        // 检查是否超过24小时
        long hoursSinceReceived = ChronoUnit.HOURS.between(item.getReceivedTime(), LocalDateTime.now());
        if (hoursSinceReceived >= 24) {
            throw new RuntimeException("已超过24小时退货期限（收货后24小时内可申请退货）");
        }
        // 检查是否已退货
        if (item.getReturnStatus() != null && item.getReturnStatus() == 2) {
            throw new RuntimeException("该商品已退货");
        }
        if (item.getReturnStatus() != null && item.getReturnStatus() == 1) {
            throw new RuntimeException("该商品正在退货流程中，请勿重复申请");
        }
        // 更新退货状态
        LambdaUpdateWrapper<OrderItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OrderItem::getItemId, itemId)
               .set(OrderItem::getReturnStatus, 1)
               .set(OrderItem::getReturnReason, reason);
        mapper.update(null, wrapper);
        // 归还库存
        Product product = productMapper.selectById(item.getProductId());
        if (product != null) {
            product.setStock(product.getStock() + item.getQuantity());
            productMapper.updateById(product);
        }
        return "退货申请已提交";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String cancelReturn(Long itemId) {
        OrderItem item = mapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("订单不存在");
        }
        if (item.getReturnStatus() == null || item.getReturnStatus() != 1) {
            throw new RuntimeException("当前没有退货申请可以取消");
        }
        // 取消退货，回退状态
        LambdaUpdateWrapper<OrderItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OrderItem::getItemId, itemId)
               .set(OrderItem::getReturnStatus, 0)
               .set(OrderItem::getReturnReason, null);
        mapper.update(null, wrapper);
        // 扣回库存
        Product product = productMapper.selectById(item.getProductId());
        if (product != null) {
            int remain = product.getStock() - item.getQuantity();
            if (remain >= 0) {
                product.setStock(remain);
                productMapper.updateById(product);
            }
        }
        return "退货申请已取消";
    }

    @Override
    public List<OrderItem> getReturnOrders(Long userId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getUserId, userId)
               .eq(OrderItem::getIsDeleted, 0)
               .in(OrderItem::getReturnStatus, 1, 2)
               .orderByDesc(OrderItem::getCreateTime);
        return mapper.selectList(wrapper);
    }
}