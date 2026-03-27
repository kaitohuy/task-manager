package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.projection.ProjectListProjection;
import com.example.taskmanager.projection.TaskStatusStats;
import com.example.taskmanager.dto.request.CreateProjectDTO;
import com.example.taskmanager.dto.response.MemberAvatarDTO;
import com.example.taskmanager.dto.response.ProjectDTO;
import com.example.taskmanager.dto.response.ProjectDashboardStatsDTO;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.ProjectMember;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.Gender;
import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.mapper.ProjectMapper;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public ProjectDTO createProject(CreateProjectDTO request, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectMapper.toEntity(request);
        project.setCreatedBy(user);
        project.setCreatedAt(LocalDateTime.now());
        project = projectRepository.save(project);

        ProjectMember pm = new ProjectMember();
        pm.setProject(project);
        pm.setUser(user);
        pm.setRole(ProjectRole.LEADER);
        projectMemberRepository.save(pm);
        return projectMapper.toDTO(project);
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findWithDetailsById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDTO(project);
    }

//    @Override
//    public Page<ProjectDTO> getAllProjects(Pageable pageable) {
//        Page<Project> projects = projectRepository.findAll(pageable);
//        return projects.map(projectMapper::toDTO);
//    }

    @Override
    public Page<ProjectDTO> getAllProjects(Pageable pageable) {
        Page<ProjectListProjection> page = projectRepository.findProjectList(pageable);

        return page.map(p -> {
            ProjectDTO dto = new ProjectDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());

            var avatars = projectRepository.findTop3Avatars(p.getId());

            dto.setMemberAvatars(
                    avatars.stream()
                            .map(a -> new MemberAvatarDTO(
                                    (String) a[0],
                                    (String) a[1],
                                    (Gender) a[2]
                            ))
                            .toList()
            );

            return dto;
        });
    }

    @Override
    public ProjectDTO updateProject(Long id, CreateProjectDTO request) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    @Override
    public Page<ProjectDTO> getProjectsByUsername(String username, Pageable pageable) {
        Page<Project> projects = projectRepository.findProjectsByMemberUsername(username, pageable);
        return projects.map(projectMapper::toDTO);
    }

    @Override
    public ProjectDashboardStatsDTO getProjectStats(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }

        long memberCount = projectMemberRepository.countByProjectId(projectId);
        TaskStatusStats stats = taskRepository.getProjectTaskStats(projectId);

        return ProjectDashboardStatsDTO.builder()
                .totalMembers(memberCount)
                .totalTasks(stats != null && stats.getTotal() != null ? stats.getTotal() : 0)
                .tasksTodo(stats != null && stats.getTodo() != null ? stats.getTodo() : 0)
                .tasksInProgress(stats != null && stats.getInProgress() != null ? stats.getInProgress() : 0)
                .tasksDone(stats != null && stats.getDone() != null ? stats.getDone() : 0)
                .tasksPaused(stats != null && stats.getPaused() != null ? stats.getPaused() : 0)
                .tasksCancelled(stats != null && stats.getCancelled() != null ? stats.getCancelled() : 0)
                .build();
    }
}
