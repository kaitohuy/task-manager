package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.security.SecurityService;
import com.example.taskmanager.dto.response.ActivityLogDTO;
import com.example.taskmanager.entity.ActivityLog;
import com.example.taskmanager.enums.ActivityAction;
import com.example.taskmanager.enums.ActivityType;
import com.example.taskmanager.mapper.ActivityLogMapper;
import com.example.taskmanager.repository.ActivityLogRepository;
import com.example.taskmanager.service.interfaces.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository logRepository;
    private final ActivityLogMapper logMapper;
    private final SecurityService securityService;

    @Override
    public Page<ActivityLogDTO> getMyLogs(Pageable pageable) {
        Long userId = getCurrentUserId();
        return logRepository.findByUserId(userId, pageable)
                .map(logMapper::toDTO);
    }

    @Override
    public Page<ActivityLogDTO> getAllLogs(Pageable pageable) {
        return logRepository.findAll(pageable).map(logMapper::toDTO);
    }

    @Override
    public Page<ActivityLogDTO> getManagerLogs(List<Long> projectIds, Pageable pageable) {
        return logRepository.findByProjectIdIn(projectIds, pageable).map(logMapper::toDTO);
    }

    @Override
    public Page<ActivityLogDTO> getByEntity(ActivityType type, Long entityId, Pageable pageable) {
        return logRepository.findByTypeAndEntityId(type, entityId, pageable)
                .map(logMapper::toDTO);
    }

    // =================== LOG CREATION ===================

    @Transactional
    public void logTaskCreated(Long userId, Long taskId, Long projectId, String taskTitle) {
        logRepository.save(buildLog(userId, ActivityAction.CREATE, ActivityType.TASK, taskId, projectId,
                "Created task: " + taskTitle));
    }

    @Transactional
    public void logTaskDeleted(Long userId, Long taskId, Long projectId) {
        logRepository.save(buildLog(userId, ActivityAction.DELETE, ActivityType.TASK, taskId, projectId,
                "Deleted task"));
    }

    @Transactional
    public void logTaskStatusChanged(Long userId, Long taskId, Long projectId, String oldStatus, String newStatus) {
        logRepository.save(buildLog(userId, ActivityAction.UPDATE_STATUS, ActivityType.TASK, taskId, projectId,
                "Changed status from " + oldStatus + " to " + newStatus));
    }

    @Transactional
    public void logTaskAssigned(Long userId, Long taskId, Long projectId, Long oldAssigneeId, Long newAssigneeId) {
        String description;
        if (oldAssigneeId == null && newAssigneeId != null) {
            description = "Assigned task to userId=" + newAssigneeId;
        } else if (oldAssigneeId != null && newAssigneeId == null) {
            description = "Unassigned task from userId=" + oldAssigneeId;
        } else if (oldAssigneeId != null && !oldAssigneeId.equals(newAssigneeId)) {
            description = "Changed assignee from userId=" + oldAssigneeId + " to userId=" + newAssigneeId;
        } else {
            description = "Assignee unchanged";
        }

        logRepository.save(buildLog(userId, ActivityAction.ASSIGN, ActivityType.TASK, taskId, projectId, description));
    }

    private ActivityLog buildLog(Long userId, ActivityAction action, ActivityType type,
                                 Long entityId, Long projectId, String description) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setProjectId(projectId);
        log.setAction(action);
        log.setType(type);
        log.setEntityId(entityId);
        log.setDescription(description);
        return log;
    }

    private void save(ActivityLog log) {
        logRepository.save(log);
    }

    private Long getCurrentUserId() {
        return securityService.getCurrentUser().getId();
    }
}