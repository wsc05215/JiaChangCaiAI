package com.jcx.jiachangcai.module.social.notification.service;

import com.jcx.jiachangcai.module.social.notification.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface INotificationService extends IService<Notification> {
    List<Notification> getByUser(Long userId);
    int getUnreadCount(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    void addNotification(Notification noti);
}
