package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.BadRequestException;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.request.CreateTaskAssignmentDTO;
import com.example.taskmanager.dto.response.TaskAssignmentDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskAssignment;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.TaskAssignmentMapper;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.TaskAssignmentRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.TaskAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskAssignmentServiceImpl implements TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskAssignmentMapper taskAssignmentMapper;

    @Override
    public TaskAssignmentDTO assignUser(CreateTaskAssignmentDTO request, Authentication authentication) {

        // 1. Lấy task
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 2. Lấy user được assign
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Lấy assignor từ token (QUAN TRỌNG)
        String username = authentication.getName();
        User assignor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Assignor not found"));

        Long projectId = task.getProject().getId();

        // 4. Check user thuộc project
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId());

        if (!isMember) {
            throw new BadRequestException("User is not in project");
        }

        // 5. Check duplicate assignment
        boolean exists = taskAssignmentRepository
                .existsByTaskIdAndUserId(task.getId(), user.getId());

        if (exists) {
            throw new BadRequestException("User already assigned to this task");
        }

        // 6. Save
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setUser(user);
        assignment.setAssignedBy(assignor);
        assignment.setCreatedAt(LocalDateTime.now());

        return taskAssignmentMapper.toDTO(taskAssignmentRepository.save(assignment));
    }

    @Override
    public List<TaskAssignmentDTO> getAssignmentsByTask(Long taskId) {
        return taskAssignmentRepository.findByTaskId(taskId)
                .stream()
                .map(taskAssignmentMapper::toDTO)
                .toList();
    }

    @Override
    public List<TaskAssignmentDTO> getMyTask(String username) {
        return taskAssignmentRepository.findByUserUsername(username)
                .stream()
                .map(taskAssignmentMapper::toDTO)
                .toList();
    }

    @Override
    public void removeAssignment(Long assignmentId) {
        if (!taskAssignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Assignment not found");
        }
        taskAssignmentRepository.deleteById(assignmentId);
    }

    @Override
    public List<TaskAssignmentDTO> getTasksByUserInProject(Long projectId, Long userId) {

        return taskAssignmentRepository.findFullByProjectAndUser(projectId, userId)
                .stream()
                .map(taskAssignmentMapper::toDTO)
                .toList();
    }
}
