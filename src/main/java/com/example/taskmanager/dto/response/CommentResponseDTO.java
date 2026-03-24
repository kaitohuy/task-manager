package com.example.taskmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private Long taskId;

    private Long userId;
    private String username;
    private String userAvatarUrl;

    private Long parentCommentId;
    private LocalDateTime createdAt;

    private Integer replyCount;
}