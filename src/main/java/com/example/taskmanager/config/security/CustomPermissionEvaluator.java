package com.example.taskmanager.config.security;

import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // Not used in this implementation but could be for object instances
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || targetType == null || !(permission instanceof String)) {
            return false;
        }

        String username = authentication.getName();
        Long id = (Long) targetId;
        String p = (String) permission;

        return switch (targetType.toUpperCase()) {
            case "PROJECT" -> handleProjectPermission(id, username, p);
            case "TASK" -> handleTaskPermission(id, username, p);
            default -> false;
        };
    }

    private boolean handleProjectPermission(Long projectId, String username, String permission) {
        return switch (permission.toUpperCase()) {
            case "MEMBER" -> projectMemberRepository.existsByProjectIdAndUserUsername(projectId, username);
            case "LEADER" -> projectMemberRepository.existsByProjectIdAndUserUsernameAndRole(projectId, username, ProjectRole.LEADER);
            default -> false;
        };
    }

    private boolean handleTaskPermission(Long taskId, String username, String permission) {
        return switch (permission.toUpperCase()) {
            case "MEMBER" -> taskRepository.existsByIdAndProjectMembersUserUsername(taskId, username);
            case "LEADER" -> taskRepository.existsLeaderByTask(taskId, username, ProjectRole.LEADER);
            default -> false;
        };
    }
}
