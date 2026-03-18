package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByCreatedById(Long userId, Pageable pageable);
    Page<Project> findByCreatedByUsername(String username, Pageable pageable);
    boolean existsByIdAndCreatedByUsername(Long projectId, String username);

}
