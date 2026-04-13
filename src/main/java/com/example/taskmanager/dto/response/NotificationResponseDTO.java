package com.example.taskmanager.dto.response;

import com.example.taskmanager.enums.NotificationType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String senderUsername;
    private String senderAvatar;
    private NotificationType type;
    private String message;
    private Long targetId;
    private boolean isRead;
    private LocalDateTime createdAt;
}