package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.response.ActivityLogDTO;
import com.example.taskmanager.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityLogService {

    Page<ActivityLogDTO> getByEntity(ActivityType type, Long entityId, Pageable pageable);

    Page<ActivityLogDTO> getMyLogs(Pageable pageable);

    Page<ActivityLogDTO> getAllLogs(Pageable pageable);

    Page<ActivityLogDTO> getManagerLogs(List<Long> projectIds, Pageable pageable);

    void logTaskCreated(Long userId, Long taskId, Long projectId, String taskTitle);

    void logTaskDeleted(Long userId, Long taskId, Long projectId);

    void logTaskStatusChanged(Long userId, Long taskId, Long projectId, String oldStatus, String newStatus);

    void logTaskAssigned(Long userId, Long taskId, Long projectId, Long oldAssigneeId, Long newAssigneeId);
}