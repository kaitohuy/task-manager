package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.request.CreateCommentDTO;
import com.example.taskmanager.dto.response.CommentResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskComment;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.CommentMapper;
import com.example.taskmanager.repository.TaskCommentRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.TaskCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getTopLevelComments(Long taskId, Pageable pageable) {
        // Ánh xạ thẳng từ Page<Entity> sang Page<DTO>
        return commentRepository.findTopLevelCommentsByTaskId(taskId, pageable)
                .map(commentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getReplies(Long parentId, Pageable pageable) {
        return commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentId, pageable).map(commentMapper::toDto);
    }

    @Override
    @Transactional
    public CommentResponseDTO addComment(Long taskId, CreateCommentDTO request, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Không tìm thấy Task"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        TaskComment comment = commentMapper.toEntity(request);
        comment.setTask(task);
        comment.setUser(user);

        // handle reply
        if (request.getParentCommentId() != null) {
            TaskComment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Comment gốc không tồn tại"));
            if (!parent.getTask().getId().equals(taskId)) {
                throw new IllegalArgumentException("Dữ liệu không hợp lệ: Trả lời chéo Task");
            }
            comment.setParentComment(parent);
        }

        TaskComment savedComment = commentRepository.save(comment);
        CommentResponseDTO responseDTO = commentMapper.toDto(comment);
        messagingTemplate.convertAndSend("/topic/tasks/" + taskId + "/comments", responseDTO);
        return responseDTO;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        TaskComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Không tìm thấy Comment"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa comment này");
        }

        commentRepository.delete(comment);
    }
}