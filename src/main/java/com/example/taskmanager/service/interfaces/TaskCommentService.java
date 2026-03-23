package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateCommentDTO;
import com.example.taskmanager.dto.response.CommentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskCommentService {
    //level 1
    Page<CommentResponseDTO> getTopLevelComments(Long taskId, Pageable pageable);
    Page<CommentResponseDTO> getReplies(Long parentId, Pageable pageable);
    CommentResponseDTO addComment(Long taskId, CreateCommentDTO request, Long userId);
    void deleteComment(Long commentId, Long userId);
}