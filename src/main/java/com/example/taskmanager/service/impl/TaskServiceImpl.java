package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.ConflictException;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.request.CreateTaskDTO;
import com.example.taskmanager.dto.request.SearchTaskDTO;
import com.example.taskmanager.dto.response.TaskDTO;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.enums.NotificationType;
import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.enums.TaskStatus;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.DashboardService;
import com.example.taskmanager.service.interfaces.NotificationService;
import com.example.taskmanager.service.interfaces.TaskService;
import com.example.taskmanager.spec.TaskSpecification;
import com.example.taskmanager.utils.PageableUtils;
import com.example.taskmanager.utils.SortUtils;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final DashboardService dashboardService;
    private final NotificationService notificationService;

    private static final Map<String, String> TASK_SORT_MAPPING = Map.of(
            "id", "id",
            "title", "title",
            "status", "status",
            "deadline", "deadline",
            "createdAt", "createdAt",
            "assigneeUsername", "assignee.username"
    );

    @Override
    public TaskDTO createTask(CreateTaskDTO request) {
        Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Task task = taskMapper.toEntity(request);
        task.setProject(project);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId()).orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }
        Task savedTask = taskRepository.save(task);
        TaskDTO responseDTO = taskMapper.toDTO(savedTask);

        messagingTemplate.convertAndSend("/topic/projects/" + request.getProjectId() + "/tasks", responseDTO);
        
        dashboardService.broadcastAdminStats();
        savedTask.getProject().getMembers().stream()
                .filter(pm -> pm.getRole() == ProjectRole.LEADER)
                .forEach(leader -> dashboardService.broadcastManagerStats(leader.getUser().getUsername()));

        if (savedTask.getAssignee() != null) {
            Notification notif = Notification.builder()
                    .recipient(savedTask.getAssignee())
                    .type(NotificationType.TASK_ASSIGNED)
                    .message("You have just assigned new task : " + savedTask.getTitle())
                    .targetId(savedTask.getId())
                    .build();
            notificationService.sendNotification(notif);
        }

        return responseDTO;
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.toDTO(task);
    }

    @Override
    public Page<TaskDTO> getTasksByProject(Long projectId, Pageable pageable) {
        pageable = PageableUtils.applyDefaultSort(
                pageable,
                Sort.by("id").descending()
        );
        pageable = SortUtils.mapSort(pageable, TASK_SORT_MAPPING);
        Page<Task> tasks = taskRepository.findByProjectId(projectId, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> getTasksByProjectAndStatus(Long projectId, TaskStatus status, Pageable pageable) {
        pageable = PageableUtils.applyDefaultSort(
                pageable,
                Sort.by("id").descending()
        );
        pageable = SortUtils.mapSort(pageable, TASK_SORT_MAPPING);
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
        pageable = PageableUtils.applyDefaultSort(
                pageable,
                Sort.by("id").descending()
        );
        pageable = SortUtils.mapSort(pageable, TASK_SORT_MAPPING);

        Specification<Task> spec = Specification.where(TaskSpecification.hasProjectId(request.getProjectId()))
                .and(TaskSpecification.hasStatus(request.getStatus()))
                .and(TaskSpecification.titleContains(request.getKeyword()))
                .and(TaskSpecification.fromDeadline(request.getDeadlineFrom()))
                .and(TaskSpecification.toDeadline(request.getDeadlineTo()));

        return taskRepository.findAll(spec, pageable).map(taskMapper::toDTO);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long id, CreateTaskDTO request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (request.getVersion() != null && !request.getVersion().equals(task.getVersion())) {
            throw new ConflictException("Task has been changed by other person, please reload!");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDeadline(request.getDeadline());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }

        try {
            Task saved = taskRepository.save(task);

            TaskDTO responseDTO = taskMapper.toDTO(saved);

            messagingTemplate.convertAndSend(
                    "/topic/projects/" + saved.getProject().getId() + "/tasks",
                    responseDTO
            );

            dashboardService.broadcastAdminStats();
            saved.getProject().getMembers().stream()
                    .filter(pm -> pm.getRole() == ProjectRole.LEADER)
                    .forEach(leader -> dashboardService.broadcastManagerStats(leader.getUser().getUsername()));

            return responseDTO;

        } catch (OptimisticLockException e) {
            throw new ConflictException("Task has been changed by other person, please reload!");
        }
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        Project project = task.getProject();
        taskRepository.deleteById(id);

        messagingTemplate.convertAndSend("/topic/projects/" + project.getId() + "/tasks/delete", id);

        dashboardService.broadcastAdminStats();
        project.getMembers().stream()
                .filter(pm -> pm.getRole() == ProjectRole.LEADER)
                .forEach(leader -> dashboardService.broadcastManagerStats(leader.getUser().getUsername()));
    }

    @Override
    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        pageable = PageableUtils.applyDefaultSort(
                pageable,
                Sort.by("id").descending()
        );
        pageable = SortUtils.mapSort(pageable, TASK_SORT_MAPPING);
        return taskRepository.findAll(pageable).map(taskMapper::toDTO);
    }

    @Override
    @Transactional
    public TaskDTO updateTaskStatus(Long id, TaskStatus status, Long version) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (version != null && !version.equals(task.getVersion())) {
            throw new ConflictException("Task has been changed by other person, please reload!");
        }

        task.setStatus(status);

        Task savedTask;
        try {
            savedTask = taskRepository.save(task);
        } catch (OptimisticLockException e) {
            throw new ConflictException("Task has been changed by other person, please reload!");
        }

        TaskDTO responseDTO = taskMapper.toDTO(savedTask);

        messagingTemplate.convertAndSend(
                "/topic/projects/" + savedTask.getProject().getId() + "/tasks",
                responseDTO
        );

        dashboardService.broadcastAdminStats();
        savedTask.getProject().getMembers().stream()
                .filter(pm -> pm.getRole() == ProjectRole.LEADER)
                .forEach(leader -> dashboardService.broadcastManagerStats(leader.getUser().getUsername()));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        User assignee = savedTask.getAssignee();

        if (currentUser != null) {
            boolean isAssigneeActing = assignee != null && assignee.getUsername().equals(currentUsername);

            if (isAssigneeActing) {
                savedTask.getProject().getMembers().stream()
                        .filter(pm -> pm.getRole() == ProjectRole.LEADER)
                        .map(ProjectMember::getUser)
                        .filter(leaderUser -> !leaderUser.getUsername().equals(currentUsername))
                        .forEach(leaderUser -> {
                            Notification notif = Notification.builder()
                                    .recipient(leaderUser)
                                    .sender(currentUser)
                                    .type(NotificationType.TASK_STATUS_CHANGED)
                                    .message(currentUser.getUsername() + " just changed task '" + savedTask.getTitle() + "' to: " + status)
                                    .targetId(savedTask.getId())
                                    .build();
                            notificationService.sendNotification(notif);
                        });
            } else {
                if (assignee != null) {
                    Notification notif = Notification.builder()
                            .recipient(assignee)
                            .sender(currentUser)
                            .type(NotificationType.TASK_STATUS_CHANGED)
                            .message(currentUser.getUsername() + " has just changed your task '" + savedTask.getTitle() + "' to: " + status)
                            .targetId(savedTask.getId())
                            .build();
                    notificationService.sendNotification(notif);
                }
            }
        }

        return responseDTO;
    }
}
