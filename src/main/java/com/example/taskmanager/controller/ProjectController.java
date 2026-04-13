package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateProjectDTO;
import com.example.taskmanager.dto.response.ProjectDTO;
import com.example.taskmanager.dto.response.ProjectDashboardStatsDTO;
import com.example.taskmanager.service.interfaces.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.getAllProjects(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody CreateProjectDTO request, Authentication authentication) {
        return ResponseEntity.ok(projectService.createProject(request, authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'MEMBER')")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<ProjectDTO>> getMyProjects(Authentication authentication, Pageable pageable) {
        String username = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectsByUsername(username, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'LEADER')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody CreateProjectDTO request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'LEADER')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'MEMBER')")
    public ResponseEntity<ProjectDashboardStatsDTO> getProjectStats(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectStats(id));
    }
}
