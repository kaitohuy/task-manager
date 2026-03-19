package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateTaskAssignmentDTO;
import com.example.taskmanager.dto.response.TaskAssignmentDTO;
import com.example.taskmanager.service.interfaces.TaskAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class TaskAssignmentController {

    private final TaskAssignmentService taskAssignmentService;

    @PostMapping
    @PreAuthorize("@projectSecurity.isLeaderByTaskId(#request.taskId, authentication)")
    public ResponseEntity<TaskAssignmentDTO> assignUser(@Valid @RequestBody CreateTaskAssignmentDTO request, Authentication authentication) {
        return ResponseEntity.ok(taskAssignmentService.assignUser(request, authentication));
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("@projectSecurity.isMemberByTaskId(#taskId, authentication)")
    public ResponseEntity<List<TaskAssignmentDTO>> getUsersByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskAssignmentService.getAssignmentsByTask(taskId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<TaskAssignmentDTO>> getMyTasks(Authentication auth) {
        return ResponseEntity.ok(taskAssignmentService.getMyTask(auth.getName()));
    }

    @GetMapping("/projects/{projectId}/users/{userId}")
    @PreAuthorize("@projectSecurity.isLeader(#projectId, authentication)")
    public ResponseEntity<List<TaskAssignmentDTO>> getUserTasksInProject(@PathVariable Long projectId, @PathVariable Long userId) {
        return ResponseEntity.ok(taskAssignmentService.getTasksByUserInProject(projectId, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@projectSecurity.isLeaderByAssignmentId(#id, authentication)")
    public ResponseEntity<Void> removeAssignment(@PathVariable Long id) {
        taskAssignmentService.removeAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
