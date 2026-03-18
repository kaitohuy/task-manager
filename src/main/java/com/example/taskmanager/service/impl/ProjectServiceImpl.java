package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.request.CreateProjectDTO;
import com.example.taskmanager.dto.response.ProjectDTO;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.ProjectMember;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.mapper.ProjectMapper;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public ProjectDTO createProject(CreateProjectDTO request) {
        User user = userRepository.findById(request.getCreatedById()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDTO(project);
    }

    @Override
    public Page<ProjectDTO> getProjectsByUser(Long userId, Pageable pageable) {
        Page<Project> projects = projectRepository.findByCreatedById(userId, pageable);
        return projects.map(projectMapper::toDTO);
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
        Page<Project> projects = projectRepository.findByCreatedByUsername(username, pageable);
        return projects.map(projectMapper::toDTO);
    }
}
