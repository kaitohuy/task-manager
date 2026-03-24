package com.example.taskmanager.repository;


import com.example.taskmanager.entity.ProjectMember;
import com.example.taskmanager.enums.ProjectRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserUsername(Long projectId, String username);
    boolean existsByProjectIdAndUserUsernameAndRole(Long projectId, String username, ProjectRole role);
    Page<ProjectMember> findByProjectId(Long projectId, Pageable pageable);
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    long countByProjectIdAndRole(Long projectId, ProjectRole role);
    long countByProjectId(Long projectId);
}