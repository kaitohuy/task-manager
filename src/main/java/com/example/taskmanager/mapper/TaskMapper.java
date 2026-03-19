package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.request.CreateTaskDTO;
import com.example.taskmanager.dto.response.TaskDTO;
import com.example.taskmanager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ProjectMapper.class})
public interface TaskMapper {
    TaskDTO toDTO(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "assignees", ignore = true)
    @Mapping(target = "project", ignore = true)
    Task toEntity(CreateTaskDTO request);
}
