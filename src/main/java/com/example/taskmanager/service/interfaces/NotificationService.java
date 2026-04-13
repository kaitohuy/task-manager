package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.response.NotificationResponseDTO;
import com.example.taskmanager.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void sendNotification(Notification notification);
    Page<NotificationResponseDTO> getUserNotifications(String username, Pageable pageable);
    long getUnreadCount(String username);
    void markAllAsRead(String username);
    void markAsRead(Long id, String username);
    void deleteNotification(Long id, String username);
    void deleteAllNotifications(String username);
}
