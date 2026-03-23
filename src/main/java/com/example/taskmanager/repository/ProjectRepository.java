package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByCreatedById(Long userId, Pageable pageable);
    Page<Project> findByCreatedByUsername(String username, Pageable pageable);
    boolean existsByIdAndCreatedByUsername(Long projectId, String username);

    @Query("""
    SELECT p FROM Project p JOIN p.members pm WHERE pm.user.username = :username
    """)
    Page<Project> findProjectsByMemberUsername(@Param("username") String username, Pageable pageable);

    @Query("""
        SELECT COUNT(p) FROM Project p 
        JOIN p.members pm 
        WHERE pm.user.username = :username 
        AND pm.role = 'LEADER'
    """)
    long countProjectsManagedByUser(@org.springframework.data.repository.query.Param("username") String username);
}
