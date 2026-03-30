package com.example.taskmanager.repository;

import com.example.taskmanager.projection.UserStatsProjection;
import com.example.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    @Query("""
        SELECT u FROM User u
        WHERE u.username = :input
           OR u.email = :input
           OR u.phone = :input
    """)
    Optional<User> findByIdentifier(String input);

    @Query("""
            SELECT 
            COUNT(u) AS totals,    
            SUM(CASE WHEN 'ADMIN' MEMBER OF u.roles THEN 1 ELSE 0 END) AS admins,
            SUM(CASE WHEN 'MANAGER' MEMBER OF u.roles THEN 1 ELSE 0 END) AS managers,
            SUM(CASE WHEN 'MEMBER' MEMBER OF u.roles THEN 1 ELSE 0 END) AS members
            FROM User u
    """)
    UserStatsProjection getUserStatisticsJPQL();

    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT pm.user.id FROM ProjectMember pm WHERE pm.project.id = :projectId)")
    Page<User> findUsersNotInProject(Long projectId, Pageable pageable);
}
