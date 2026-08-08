package com.jcx.jiachangcai.module.social.notification.controller;

import com.jcx.jiachangcai.module.social.notification.entity.Notification;
import com.jcx.jiachangcai.module.social.notification.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private INotificationService service;

    @GetMapping("/list")
    public List<Notification> getList(@RequestParam Long userId) {
        return service.getByUser(userId);
    }

    @GetMapping("/unreadCount")
    public int getUnreadCount(@RequestParam Long userId) {
        return service.getUnreadCount(userId);
    }

    @PostMapping("/markRead")
    public String markRead(@RequestParam Long notificationId) {
        service.markAsRead(notificationId);
        return "ok";
    }

    @PostMapping("/markAllRead")
    public String markAllRead(@RequestParam Long userId) {
        service.markAllAsRead(userId);
        return "ok";
    }
}
