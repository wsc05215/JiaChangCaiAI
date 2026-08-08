package com.jcx.jiachangcai.module.social.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jcx.jiachangcai.module.social.notification.entity.Notification;
import com.jcx.jiachangcai.module.social.notification.mapper.NotificationMapper;
import com.jcx.jiachangcai.module.social.notification.service.INotificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {

    @Autowired
    private NotificationMapper mapper;

    @Override
    public List<Notification> getByUser(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .orderByDesc(Notification::getCreateTime);
        return mapper.selectList(wrapper);
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0);
        return mapper.selectCount(wrapper).intValue();
    }

    @Override
    public void markAsRead(Long notificationId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, notificationId)
               .set(Notification::getIsRead, 1);
        mapper.update(null, wrapper);
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0)
               .set(Notification::getIsRead, 1);
        mapper.update(null, wrapper);
    }

    @Override
    public void addNotification(Notification noti) {
        noti.setCreateTime(LocalDateTime.now());
        noti.setIsRead(0);
        mapper.insert(noti);
    }
}
