package com.jcx.jiachangcai.module.order.order_item.service;

import com.jcx.jiachangcai.module.order.order_item.entity.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单明细表 服务类
 * </p>
 *
 * @author wsc
 * @since 2026-08-03
 */
import com.jcx.jiachangcai.module.order.order_item.entity.OrderDetailVO;
import java.util.List;

public interface IOrderItemService extends IService<OrderItem> {
     String addOrder(OrderItem orderItem);
     List<OrderItem> getOrderitem(Long userId);
     String confirmReceive(Long itemId);
     String requestReturn(Long itemId, String reason);
     String cancelReturn(Long itemId);
     String requestBatchReturn(List<Long> itemIds, String reason);
     List<OrderItem> getReturnOrders(Long userId);
     OrderDetailVO getOrderDetail(Long orderId);
}
