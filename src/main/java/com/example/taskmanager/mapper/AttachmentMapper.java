package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.request.CreateAttachmentDTO;
import com.example.taskmanager.dto.response.AttachmentResponseDTO;
import com.example.taskmanager.entity.TaskAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    AttachmentResponseDTO toDto(TaskAttachment taskAttachment);

    TaskAttachment toEntity(CreateAttachmentDTO dto);
}