package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.BadRequestException;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.response.NotificationResponseDTO;
import com.example.taskmanager.entity.Notification;
import com.example.taskmanager.mapper.NotificationMapper;
import com.example.taskmanager.repository.NotificationRepository;
import com.example.taskmanager.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void sendNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        NotificationResponseDTO dto = notificationMapper.toDto(saved);

        messagingTemplate.convertAndSendToUser(
                saved.getRecipient().getUsername(),
                "/queue/notifications",
                dto
        );
    }

    @Override
    public Page<NotificationResponseDTO> getUserNotifications(String username, Pageable pageable) {
        return notificationRepository.findByRecipientUsernameOrderByCreatedAtDesc(username, pageable).map(notificationMapper::toDto);
    }

    @Override
    public long getUnreadCount(String username) {
        return notificationRepository.countByRecipientUsernameAndIsReadFalse(username);
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        notificationRepository.markAllAsReadByUsername(username);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, String username) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        if (!notification.getRecipient().getUsername().equals(username)) {
            throw new BadRequestException("You don't have permission on this notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id, String username) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getRecipient().getUsername().equals(username)) {
            throw new BadRequestException("You don't have permission to delete this notification");
        }
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(String username) {
        notificationRepository.deleteByRecipientUsername(username);
    }

}