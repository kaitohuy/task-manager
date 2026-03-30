package com.example.taskmanager.repository;

import com.example.taskmanager.projection.MemberAvatarProjection;
import com.example.taskmanager.projection.ProjectListProjection;
import com.example.taskmanager.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @EntityGraph(attributePaths = {"createdBy", "members", "members.user"})
    Optional<Project> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"createdBy", "members", "members.user"})
    Page<Project> findAll(Pageable pageable);

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

    @Query("""
        SELECT p.id as id,
               p.name as name,
               p.description as description,
               u.username as createdByUsername
        FROM Project p
        JOIN p.createdBy u
    """)
    Page<ProjectListProjection> findProjectList(Pageable pageable);

}
