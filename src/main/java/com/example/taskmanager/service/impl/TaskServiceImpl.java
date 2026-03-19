package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.request.CreateTaskDTO;
import com.example.taskmanager.dto.request.SearchTaskDTO;
import com.example.taskmanager.dto.response.TaskDTO;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.enums.TaskStatus;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.interfaces.TaskService;
import com.example.taskmanager.spec.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;


    @Override
    public TaskDTO createTask(CreateTaskDTO request) {
        Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new RuntimeException("Project not found"));
        Task task = taskMapper.toEntity(request);
        task.setProject(project);
        task.setCreatedAt(LocalDateTime.now());
        task = taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.toDTO(task);
    }

    @Override
    public Page<TaskDTO> getTasksByProject(Long projectId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByProjectId(projectId, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> getTasksByProjectAndStatus(Long projectId, TaskStatus status, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByProjectIdAndStatus(projectId, status, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> searchTasks(Long projectId, String keyword, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByProjectIdAndTitleContaining(projectId, keyword, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> searchTasks(SearchTaskDTO request, Pageable pageable) {
        Specification<Task> spec = Specification.where((Specification<Task>) null);

        if (request.getProjectId() != null) {
            spec = spec.and(TaskSpecification.hasProjectId(request.getProjectId()));
        }
        if (request.getStatus() != null) {
            spec = spec.and(TaskSpecification.hasStatus(request.getStatus()));
        }
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            spec = spec.and(TaskSpecification.titleContains(request.getKeyword()));
        }
        if (request.getDeadlineFrom() != null) {
            spec = spec.and(TaskSpecification.fromDeadline(request.getDeadlineFrom()));
        }
        if (request.getDeadlineTo() != null) {
            spec = spec.and(TaskSpecification.toDeadline(request.getDeadlineTo()));
        }
        Page<Task> tasks = taskRepository.findAll(spec, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Override
    public TaskDTO updateTask(Long id, CreateTaskDTO request) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDeadline(request.getDeadline());
        task = taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }
}
