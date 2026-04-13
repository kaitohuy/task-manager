package com.example.taskmanager.dto.response;

import com.example.taskmanager.enums.ActivityAction;
import com.example.taskmanager.enums.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityLogDTO {

    private Long id;
    private Long userId;
    private String username;
    private ActivityAction action;
    private ActivityType type;
    private Long entityId;
    private String entityName;
    private String description;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;
}