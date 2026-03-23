package com.example.taskmanager.repository;

import com.example.taskmanager.entity.TaskAssignment;
import com.example.taskmanager.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    List<TaskAssignment> findByUserId(Long userId);
    List<TaskAssignment> findByTaskId(Long taskId);
    List<TaskAssignment> findByUserUsername(String username);
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);
    List<TaskAssignment> findByTaskProjectIdAndUserId(Long projectId, Long userId);

    @Query("""
    SELECT COUNT(ta) > 0 FROM TaskAssignment ta
    JOIN ta.task t
    JOIN t.project p
    JOIN p.members pm
    WHERE ta.id = :assignmentId
    AND pm.user.username = :username
    AND pm.role = :role
    """)
    boolean existsLeaderByAssignment(Long assignmentId, String username, ProjectRole role);

    @Query("""
    SELECT ta FROM TaskAssignment ta
    JOIN FETCH ta.user
    JOIN FETCH ta.task t
    JOIN FETCH t.project
    WHERE t.project.id = :projectId
    AND ta.user.id = :userId
    """)
    List<TaskAssignment> findFullByProjectAndUser(Long projectId, Long userId);
}