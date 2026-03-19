package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateTaskDTO;
import com.example.taskmanager.dto.request.SearchTaskDTO;
import com.example.taskmanager.dto.response.TaskDTO;
import com.example.taskmanager.enums.TaskStatus;
import com.example.taskmanager.service.interfaces.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("@projectSecurity.isLeader(#request.projectId, authentication)")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody CreateTaskDTO request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@projectSecurity.isMemberByTaskId(#id, authentication)")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("@projectSecurity.isMember(#projectId, authentication)")
    public ResponseEntity<Page<TaskDTO>> getTasksByProject(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, pageable));
    }

    @GetMapping("/filter")
    @PreAuthorize("@projectSecurity.isMember(#projectId, authentication)")
    public ResponseEntity<Page<TaskDTO>> getTasksByStatus(@RequestParam Long projectId, @RequestParam TaskStatus status, Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByProjectAndStatus(projectId, status, pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("@projectSecurity.isMember(#projectId, authentication)")
    public ResponseEntity<Page<TaskDTO>> searchTasks(@RequestParam Long projectId, @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(taskService.searchTasks(projectId, keyword, pageable));
    }

    @GetMapping("/search-advanced")
    @PreAuthorize("@projectSecurity.isMember(#projectId, authentication)")
    public ResponseEntity<Page<TaskDTO>> searchTasks(SearchTaskDTO request, Pageable pageable) {
        return ResponseEntity.ok(taskService.searchTasks(request, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@projectSecurity.isLeaderByTaskId(#id, authentication)")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody CreateTaskDTO request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@projectSecurity.isLeaderByTaskId(#id, authentication)")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}