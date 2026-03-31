package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.ConflictException;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.config.security.SecurityService;
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
import com.example.taskmanager.service.interfaces.ActivityLogService;
import com.example.taskmanager.service.interfaces.DashboardService;
import com.example.taskmanager.service.interfaces.NotificationService;
import com.example.taskmanager.service.interfaces.TaskService;
import com.example.taskmanager.spec.TaskSpecification;
import com.example.taskmanager.utils.PageableUtils;
import com.example.taskmanager.utils.SortUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
    private final ActivityLogService activityLogService;
    private final EntityManager entityManager;
    private final SecurityService securityService;
    private final ConcurrentMap<Long, String> userCache = new ConcurrentHashMap<>();

    private static final Map<String, String> TASK_SORT_MAPPING = Map.of(
            "id", "id",
            "title", "title",
            "status", "status",
            "deadline", "deadline",
            "createdAt", "createdAt",
            "assigneeUsername", "assignee.username"
    );

    private void broadcastDashboard(Project project) {
        dashboardService.broadcastAdminStats();
        project.getMembers().stream()
                .filter(pm -> pm.getRole() == ProjectRole.LEADER)
                .forEach(leader -> dashboardService.broadcastManagerStats(leader.getUser().getUsername()));
    }

    private User resolveAssignee(Long assigneeId) {
        if (assigneeId == null) return null;
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found: " + assigneeId));
    }

    private Pageable applyTaskPageable(Pageable pageable) {
        pageable = PageableUtils.applyDefaultSort(pageable, Sort.by("id").descending());
        return SortUtils.mapSort(pageable, TASK_SORT_MAPPING);
    }

    private String getLastModifiedByUsername(Long taskId) {
        try {
            AuditReader reader = AuditReaderFactory.get(entityManager);
            List<Number> revisions = reader.getRevisions(Task.class, taskId);

            // Nếu không có lịch sử, trả về "system" hoặc "unknown" thay vì tiếp tục xử lý
            if (revisions == null || revisions.isEmpty()) {
                System.out.println("null revisions");
                return "unknown";
            }

            // Lấy revision cuối cùng
            System.out.println("lastestRev: " + (revisions.size()-1));
            Number latestRev = revisions.get(revisions.size() - 1);

            // Kiểm tra nếu revision nhỏ hơn hoặc bằng 0 (tránh lỗi negative)
            if (latestRev.longValue() <= 0) return "unknown";

            // Sử dụng Try-Catch riêng cho đoạn đọc Revision Entity
            try {
                AuditRevisionEntity revEntity = reader.findRevision(AuditRevisionEntity.class, latestRev);
                return (revEntity != null && revEntity.getUsername() != null)
                        ? revEntity.getUsername()
                        : "unknown";
            } catch (Exception e) {
                return "unknown";
            }
        } catch (Exception e) {
            return "unknown"; // Trả về mặc định nếu có bất cứ lỗi Envers nào
        }
    }

    private void sendNotification(User recipient, User sender,
                                  NotificationType type, String message, Long targetId) {
        Notification notif = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(type)
                .message(message)
                .targetId(targetId)
                .build();
        notificationService.sendNotification(notif);
    }

    private void notifyLeaders(Project project, User sender,
                               NotificationType type, String message, Long targetId) {
        project.getMembers().stream()
                .filter(pm -> pm.getRole() == ProjectRole.LEADER)
                .map(ProjectMember::getUser)
                .filter(leader -> !leader.getUsername().equals(sender.getUsername()))
                .forEach(leader -> sendNotification(leader, sender, type, message, targetId));
    }

    @Override
    @Transactional
    public TaskDTO createTask(CreateTaskDTO request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Task task = taskMapper.toEntity(request);
        task.setProject(project);
        task.setAssignee(resolveAssignee(request.getAssigneeId()));

        Task savedTask = taskRepository.save(task);
        User currentUser = securityService.getCurrentUser();

        activityLogService.logTaskCreated(
                currentUser.getId(),
                savedTask.getId(),
                project.getId(),
                savedTask.getTitle()
        );

        TaskDTO responseDTO = taskMapper.toDTO(savedTask);
        messagingTemplate.convertAndSend("/topic/projects/" + project.getId() + "/tasks", responseDTO);
        broadcastDashboard(project);

        if (savedTask.getAssignee() != null) {
            sendNotification(
                    savedTask.getAssignee(),
                    currentUser,
                    NotificationType.TASK_ASSIGNED,
                    "You have just been assigned a new task: " + savedTask.getTitle(),
                    savedTask.getId()
            );
        }

        return responseDTO;
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return taskMapper.toDTO(task);
    }

    @Override
    public Page<TaskDTO> getTasksByProject(Long projectId, Pageable pageable) {
        pageable = applyTaskPageable(pageable);
        return taskRepository.findByProjectId(projectId, pageable).map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> getTasksByProjectAndStatus(Long projectId, TaskStatus status, Pageable pageable) {
        pageable = applyTaskPageable(pageable);
        return taskRepository.findByProjectIdAndStatus(projectId, status, pageable).map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> searchTasks(Long projectId, String keyword, Pageable pageable) {
        return taskRepository.findByProjectIdAndTitleContaining(projectId, keyword, pageable)
                .map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> searchTasks(SearchTaskDTO request, Pageable pageable) {
        pageable = applyTaskPageable(pageable);
        Specification<Task> spec = Specification
                .where(TaskSpecification.hasProjectId(request.getProjectId()))
                .and(TaskSpecification.hasStatus(request.getStatus()))
                .and(TaskSpecification.titleContains(request.getKeyword()))
                .and(TaskSpecification.fromDeadline(request.getDeadlineFrom()))
                .and(TaskSpecification.toDeadline(request.getDeadlineTo()));
        return taskRepository.findAll(spec, pageable).map(taskMapper::toDTO);
    }

    @Override
    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        pageable = applyTaskPageable(pageable);
        return taskRepository.findAll(pageable).map(taskMapper::toDTO);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long id, CreateTaskDTO request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (request.getVersion() != null && !request.getVersion().equals(task.getVersion())) {
            String modifier = getLastModifiedByUsername(id);
            throw new ConflictException(
                    "Task has been modified by \"" + modifier + "\", please reload!"
            );
        }

        User currentUser = securityService.getCurrentUser();
        User oldAssignee = task.getAssignee();
        User newAssignee = resolveAssignee(request.getAssigneeId());

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDeadline(request.getDeadline());
        task.setAssignee(newAssignee);

        try {
            Task saved = taskRepository.save(task);

            boolean assigneeChanged = !Objects.equals(
                    oldAssignee != null ? oldAssignee.getId() : null,
                    newAssignee != null ? newAssignee.getId() : null
            );
            if (assigneeChanged) {
                activityLogService.logTaskAssigned(
                        currentUser.getId(),
                        saved.getId(),
                        saved.getProject().getId(),
                        oldAssignee != null ? oldAssignee.getId() : null,
                        newAssignee != null ? newAssignee.getId() : null
                );
            }

            TaskDTO responseDTO = taskMapper.toDTO(saved);
            messagingTemplate.convertAndSend(
                    "/topic/projects/" + saved.getProject().getId() + "/tasks",
                    responseDTO
            );
            broadcastDashboard(saved.getProject());

            return responseDTO;

        } catch (OptimisticLockException e) {
            String modifier = getLastModifiedByUsername(id);
            throw new ConflictException(
                    "Task has been modified by \"" + modifier + "\", please reload!"
            );
        }
    }

    @Override
    @Transactional
    public TaskDTO updateTaskStatus(Long id, TaskStatus status, Long version) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (version != null && !version.equals(task.getVersion())) {
            String modifier = getLastModifiedByUsername(id);
            throw new ConflictException(
                    "Task has been modified by \"" + modifier + "\", please reload!"
            );
        }

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(status);

        try {
            Task savedTask = taskRepository.save(task);
            User currentUser = securityService.getCurrentUser();

            activityLogService.logTaskStatusChanged(
                    currentUser.getId(),
                    savedTask.getId(),
                    savedTask.getProject().getId(),
                    oldStatus.name(),
                    status.name()
            );

            TaskDTO responseDTO = taskMapper.toDTO(savedTask);
            messagingTemplate.convertAndSend(
                    "/topic/projects/" + savedTask.getProject().getId() + "/tasks",
                    responseDTO
            );
            broadcastDashboard(savedTask.getProject());

            User assignee = savedTask.getAssignee();
            String currentUsername = currentUser.getUsername();
            boolean isAssigneeActing = assignee != null && assignee.getUsername().equals(currentUsername);
            String taskTitle = savedTask.getTitle();
            Long taskId = savedTask.getId();

            if (isAssigneeActing) {
                notifyLeaders(
                        savedTask.getProject(),
                        currentUser,
                        NotificationType.TASK_STATUS_CHANGED,
                        currentUsername + " just changed task '" + taskTitle + "' to: " + status,
                        taskId
                );
            } else if (assignee != null) {
                sendNotification(
                        assignee,
                        currentUser,
                        NotificationType.TASK_STATUS_CHANGED,
                        currentUsername + " has just changed your task '" + taskTitle + "' to: " + status,
                        taskId
                );
            }
            return responseDTO;

        } catch (OptimisticLockException e) {
            String modifier = getLastModifiedByUsername(id);
            throw new ConflictException(
                    "Task has been modified by \"" + modifier + "\", please reload!"
            );
        }
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        Project project = task.getProject();
        User currentUser = securityService.getCurrentUser();

        taskRepository.delete(task);

        activityLogService.logTaskDeleted(
                currentUser.getId(),
                task.getId(),
                project.getId()
        );

        messagingTemplate.convertAndSend("/topic/projects/" + project.getId() + "/tasks/delete", id);
        broadcastDashboard(project);
    }
}