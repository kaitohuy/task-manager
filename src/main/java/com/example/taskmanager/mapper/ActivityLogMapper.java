package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.response.ActivityLogDTO;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.ActivityAction;
import com.example.taskmanager.enums.ActivityType;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ActivityLogMapper {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public ActivityLogDTO toDTO(com.example.taskmanager.entity.ActivityLog log) {
        ActivityLogDTO dto = new ActivityLogDTO();

        dto.setId(log.getId());
        dto.setUserId(log.getUserId());
        dto.setUsername(userRepository.findById(log.getUserId())
                .map(User::getUsername)
                .orElse("unknown"));
        dto.setAction(log.getAction());
        dto.setType(log.getType());
        dto.setEntityId(log.getEntityId());
        dto.setDescription(log.getDescription());
        dto.setCreatedAt(log.getCreatedAt());

        String entityName = "";
        if (log.getType() == ActivityType.TASK) {
            entityName = taskRepository.findById(log.getEntityId())
                    .map(Task::getTitle)
                    .orElse("");
        } else if (log.getType() == ActivityType.PROJECT) {
            entityName = projectRepository.findById(log.getEntityId())
                    .map(Project::getName)
                    .orElse("");
        }
        dto.setEntityName(entityName);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("info", log.getDescription());
        metadata.put("entityName", entityName);

        if (log.getAction() == ActivityAction.ASSIGN) {
            String desc = log.getDescription();
            try {
                String[] parts = desc.split("userId=");
                if (parts.length > 1) {
                    Long assigneeId = Long.parseLong(parts[1].trim());
                    metadata.put("assigneeId", assigneeId);
                    userRepository.findById(assigneeId).ifPresent(u -> metadata.put("assigneeName", u.getUsername()));
                }
            } catch (Exception ignored) {}
        }

        if (log.getAction() == ActivityAction.UPDATE_STATUS) {
            String desc = log.getDescription();
            if (desc.contains("from") && desc.contains("to")) {
                String[] parts = desc.split("from|to");
                if (parts.length >= 3) {
                    metadata.put("oldStatus", parts[1].trim());
                    metadata.put("newStatus", parts[2].trim());
                }
            }
        }

        if (log.getAction() == ActivityAction.CREATE || log.getAction() == ActivityAction.DELETE) {
            metadata.put("entityId", log.getEntityId());
        }

        dto.setMetadata(metadata);

        return dto;
    }
}