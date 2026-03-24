package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.request.CreateProjectDTO;
import com.example.taskmanager.dto.response.MemberAvatarDTO;
import com.example.taskmanager.dto.response.ProjectDTO;
import com.example.taskmanager.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMapper {

    @Mapping(target = "memberAvatars", expression = "java(mapAvatars(project))")
    ProjectDTO toDTO(Project project);

    Project toEntity(CreateProjectDTO request);

    default List<MemberAvatarDTO> mapAvatars(Project project) {
        if (project == null || project.getMembers() == null) {
            return Collections.emptyList();
        }
        return project.getMembers().stream()
                .limit(3)
                .map(m -> new MemberAvatarDTO(m.getUser().getUsername(), m.getUser().getImageUrl(), m.getUser().getGender()))
                .collect(Collectors.toList());
    }
}