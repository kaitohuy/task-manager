package com.example.taskmanager.repository;

import com.example.taskmanager.dto.response.ProjectMemberDTO;
import com.example.taskmanager.entity.ProjectMember;
import com.example.taskmanager.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserUsername(Long projectId, String username);
    boolean existsByProjectIdAndUserUsernameAndRole(Long projectId, String username, ProjectRole role);
    List<ProjectMember> findByProjectId(Long projectId);
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
}