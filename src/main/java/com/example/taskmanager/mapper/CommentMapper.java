package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.request.CreateCommentDTO;
import com.example.taskmanager.dto.response.CommentResponseDTO;
import com.example.taskmanager.entity.TaskComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.imageUrl", target = "userAvatarUrl")
    @Mapping(source = "parentComment.id", target = "parentCommentId")
    CommentResponseDTO toDto(TaskComment taskComment);

    @Mapping(target = "parentComment", ignore = true)
    TaskComment toEntity(CreateCommentDTO dto);
}