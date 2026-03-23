package com.example.taskmanager.dto.response;

import com.example.taskmanager.enums.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponseDTO {
    private Long id;
    private Long taskId;

    private Long userId;
    private String username;

    private String name;
    private String url;
    private AttachmentType type;
    private LocalDateTime uploadedAt;
}