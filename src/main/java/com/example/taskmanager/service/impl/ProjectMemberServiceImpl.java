package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.BadRequestException;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.request.AddMemberDTO;
import com.example.taskmanager.dto.response.ProjectMemberDTO;
import com.example.taskmanager.dto.response.UserDTO;
import com.example.taskmanager.entity.Notification;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.ProjectMember;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.NotificationType;
import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.mapper.ProjectMemberMapper;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.NotificationService;
import com.example.taskmanager.service.interfaces.ProjectMemberService;
import com.example.taskmanager.utils.PageableUtils;
import com.example.taskmanager.utils.SortUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final EntityManager entityManager;

    private static final Map<String, String> MEMBER_SORT_MAPPING = Map.of(
            "id", "id",
            "joinedAt", "joinedAt",
            "role", "role",
            "username", "user.username"
    );

    private static final Map<String, String> USER_SORT_MAPPING = Map.of(
            "id", "id",
            "username", "username",
            "email", "email"
    );

    @Override
    public ProjectMemberDTO addMember(Long projectId, AddMemberDTO request) {

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean exists = projectMemberRepository.existsByProjectIdAndUserUsername(projectId, user.getUsername());

        if (exists) {
            throw new BadRequestException("User already in project");
        }

        ProjectMember pm = new ProjectMember();
        pm.setProject(project);
        pm.setUser(user);
        pm.setRole(request.getRole());

        projectMemberRepository.save(pm);

        Notification notif = Notification.builder()
                .recipient(user)
                .type(NotificationType.PROJECT_INVITE)
                .message("You have just added to project : " + project.getName())
                .targetId(project.getId())
                .build();
        notificationService.sendNotification(notif);

        return projectMemberMapper.toDTO(pm);
    }

    @Override
    public void removeMember(Long projectId, Long userId) {
        ProjectMember pm = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found")
        );
        projectMemberRepository.delete(pm);
    }

    @Override
    public Page<ProjectMemberDTO> getMembers(Long projectId, Pageable pageable) {

        pageable = PageableUtils.applyDefaultSort(
                pageable,
                Sort.by("joinedAt").descending()
        );

        pageable = SortUtils.mapSort(pageable, MEMBER_SORT_MAPPING);

        return projectMemberRepository
                .findByProjectId(projectId, pageable)
                .map(projectMemberMapper::toDTO);
    }

    @Override
    @Transactional
    public ProjectMemberDTO updateMemberRole(Long projectId, Long userId, ProjectRole newRole) {
        ProjectMember pm = projectMemberRepository.findByProjectIdAndUserId(projectId, userId).orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (pm.getRole() == ProjectRole.LEADER && newRole == ProjectRole.MEMBER) {
            long leaderCount = projectMemberRepository.countByProjectIdAndRole(projectId, ProjectRole.LEADER);
            if (leaderCount <= 1) {
                throw new BadRequestException("Cannot demote the last leader of the project");
            }
        }

        pm.setRole(newRole);
        return projectMemberMapper.toDTO(projectMemberRepository.save(pm));
    }

    @Override
    public Page<UserDTO> getAvailableUsersToAdd(Long projectId, Pageable pageable) {

        pageable = PageableUtils.applyDefaultSort(
                pageable,
                Sort.by("username").ascending()
        );

        pageable = SortUtils.mapSort(pageable, USER_SORT_MAPPING);

        return userRepository.findUsersNotInProject(projectId, pageable)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional
    public void addMembersBatch(Long projectId, List<Long> userIds) {

        int batchSize = 50;

        Project project = entityManager.getReference(Project.class, projectId);

        for (int i = 0; i < userIds.size(); i++) {

            User user = entityManager.getReference(User.class, userIds.get(i));

            ProjectMember pm = new ProjectMember();
            pm.setProject(project);
            pm.setUser(user);
            pm.setRole(ProjectRole.MEMBER);

            entityManager.persist(pm);

            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.flush();
        entityManager.clear();
    }
}
