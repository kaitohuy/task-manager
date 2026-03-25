package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.response.MeetingResponseDTO;
import com.example.taskmanager.entity.Meeting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MeetingMapper {

    @Mapping(source = "project.id", target = "projectId")
    MeetingResponseDTO toDto(Meeting meeting);
}