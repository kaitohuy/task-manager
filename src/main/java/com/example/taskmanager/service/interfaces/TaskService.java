package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateTaskDTO;
import com.example.taskmanager.dto.request.SearchTaskDTO;
import com.example.taskmanager.dto.response.TaskDTO;
import com.example.taskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDTO createTask(CreateTaskDTO request);
    TaskDTO getTaskById(Long id);
    Page<TaskDTO> getTasksByProject(Long projectId, Pageable pageable);
    Page<TaskDTO> getTasksByProjectAndStatus(Long projectId, TaskStatus status, Pageable pageable);
    Page<TaskDTO> searchTasks(Long projectId, String keyword, Pageable pageable);
    Page<TaskDTO> searchTasks(SearchTaskDTO request, Pageable pageable);
    TaskDTO updateTask(Long id, CreateTaskDTO request);
    void deleteTask(Long id);
}