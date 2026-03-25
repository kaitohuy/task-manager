package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.response.NotificationResponseDTO;
import com.example.taskmanager.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "sender.username", target = "senderUsername")
    @Mapping(source = "sender.imageUrl", target = "senderAvatar")
    NotificationResponseDTO toDto(Notification notification);
}