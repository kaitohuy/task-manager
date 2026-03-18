package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateProjectDTO;
import com.example.taskmanager.dto.response.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDTO createProject(CreateProjectDTO request);
    ProjectDTO getProjectById(Long id);
    Page<ProjectDTO> getProjectsByUser(Long userId, Pageable pageable);
    ProjectDTO updateProject(Long id, CreateProjectDTO request);
    void deleteProject(Long id);
    Page<ProjectDTO> getProjectsByUsername(String username, Pageable pageable);
}
