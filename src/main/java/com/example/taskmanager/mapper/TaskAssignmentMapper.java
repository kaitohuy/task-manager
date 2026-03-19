package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.response.TaskAssignmentDTO;
import com.example.taskmanager.entity.TaskAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskAssignmentMapper {

    @Mapping(source = "task.id", target = "taskId")
    TaskAssignmentDTO toDTO(TaskAssignment entity);

}
