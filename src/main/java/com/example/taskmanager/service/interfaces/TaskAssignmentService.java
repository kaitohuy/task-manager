package com.example.taskmanager.service.interfaces;


import com.example.taskmanager.dto.request.CreateTaskAssignmentDTO;
import com.example.taskmanager.dto.response.TaskAssignmentDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TaskAssignmentService {
    TaskAssignmentDTO assignUser(CreateTaskAssignmentDTO request, Authentication authentication);
    List<TaskAssignmentDTO> getAssignmentsByTask(Long taskId);
    void removeAssignment(Long assignmentId);
    List<TaskAssignmentDTO> getMyTask(String username);
    List<TaskAssignmentDTO> getTasksByUserInProject(Long projectId, Long userId);
}
