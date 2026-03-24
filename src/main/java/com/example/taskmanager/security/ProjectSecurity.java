package com.example.taskmanager.security;

import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurity {

    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    public boolean isMember(Long projectId, Authentication authentication) {
        return projectMemberRepository.existsByProjectIdAndUserUsername(
                projectId, authentication.getName()
        );
    }

    public boolean isLeader(Long projectId, Authentication authentication) {
        return projectMemberRepository.existsByProjectIdAndUserUsernameAndRole(
                projectId, authentication.getName(), ProjectRole.LEADER
        );
    }

    public boolean isMemberByTaskId(Long taskId, Authentication auth) {
        return taskRepository.existsByIdAndProjectMembersUserUsername(
                taskId, auth.getName()
        );
    }

    public boolean isLeaderByTaskId(Long taskId, Authentication auth) {
        return taskRepository.existsLeaderByTask(
                taskId,
                auth.getName(),
                ProjectRole.LEADER
        );
    }
}