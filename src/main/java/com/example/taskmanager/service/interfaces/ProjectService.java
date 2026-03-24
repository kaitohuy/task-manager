package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateProjectDTO;
import com.example.taskmanager.dto.response.ProjectDTO;
import com.example.taskmanager.dto.response.ProjectDashboardStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ProjectService {
    ProjectDTO createProject(CreateProjectDTO request, Authentication authentication);
    ProjectDTO getProjectById(Long id);
    Page<ProjectDTO> getAllProjects(Pageable pageable);
    ProjectDTO updateProject(Long id, CreateProjectDTO request);
    void deleteProject(Long id);
    Page<ProjectDTO> getProjectsByUsername(String username, Pageable pageable);
    ProjectDashboardStatsDTO getProjectStats(Long projectId);
}
