package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateCommentDTO;
import com.example.taskmanager.dto.response.CommentResponseDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService commentService;
    private final UserRepository userRepository;

    @GetMapping("/tasks/{taskId}/comments")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isMemberByTaskId(#taskId, authentication)")
    public ResponseEntity<Page<CommentResponseDTO>> getTopLevelComments(
            @PathVariable Long taskId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(commentService.getTopLevelComments(taskId, pageable));
    }

    @GetMapping("/comments/{parentId}/replies")
    public ResponseEntity<Page<CommentResponseDTO>> getReplies(
            @PathVariable Long parentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(commentService.getReplies(parentId, pageable));
    }

    @PostMapping("/tasks/{taskId}/comments")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isMemberByTaskId(#taskId, authentication)")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentDTO request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(commentService.addComment(taskId, request, userId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found from Token"));
    }
}