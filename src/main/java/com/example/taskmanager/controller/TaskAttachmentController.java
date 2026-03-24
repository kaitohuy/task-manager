package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateAttachmentDTO;
import com.example.taskmanager.dto.response.AttachmentResponseDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.TaskAttachmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskAttachmentController {

    private final TaskAttachmentService attachmentService;
    private final UserRepository userRepository;

    @GetMapping("/tasks/{taskId}/attachments")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isMemberByTaskId(#taskId, authentication)")
    public ResponseEntity<List<AttachmentResponseDTO>> getAttachments(@PathVariable Long taskId) {
        return ResponseEntity.ok(attachmentService.getAttachments(taskId));
    }

    @PostMapping("/tasks/{taskId}/attachments")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isMemberByTaskId(#taskId, authentication)")
    public ResponseEntity<AttachmentResponseDTO> addAttachment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateAttachmentDTO request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(attachmentService.addAttachment(taskId, request, userId));
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long attachmentId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        attachmentService.deleteAttachment(attachmentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/{taskId}/attachments/upload")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isMemberByTaskId(#taskId, authentication)")
    public ResponseEntity<AttachmentResponseDTO> uploadFile(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(attachmentService.uploadFile(taskId, file, userId));
    }

    private Long extractUserId(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found from Token"));
    }
}