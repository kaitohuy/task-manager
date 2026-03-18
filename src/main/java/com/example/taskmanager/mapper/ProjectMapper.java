package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.request.CreateProjectDTO;
import com.example.taskmanager.dto.response.ProjectDTO;
import com.example.taskmanager.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMapper {
    ProjectDTO toDTO(Project project);

    @Mapping(target = "createdBy.id", source = "createdById")
    Project toEntity(CreateProjectDTO request);
}
