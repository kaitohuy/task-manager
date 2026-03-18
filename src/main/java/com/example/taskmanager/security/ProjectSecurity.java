package com.example.taskmanager.security;

import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurity {

    private final ProjectMemberRepository projectMemberRepository;

    public boolean isMember(Long projectId, Authentication authentication) {
        String username = authentication.getName();
        return projectMemberRepository.existsByProjectIdAndUserUsername(projectId, username);
    }

    public boolean isLeader(Long projectId, Authentication authentication) {
        String username = authentication.getName();
        return projectMemberRepository.existsByProjectIdAndUserUsernameAndRole(projectId, username, ProjectRole.LEADER);
    }
}