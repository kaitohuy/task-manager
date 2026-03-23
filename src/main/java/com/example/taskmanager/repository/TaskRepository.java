package com.example.taskmanager.repository;

import com.example.taskmanager.dto.projection.TaskStatsProjection;
import com.example.taskmanager.dto.projection.TaskStatusStats;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);
    Page<Task> findByProjectIdAndTitleContaining(Long projectId, String keyword, Pageable pageable);

    boolean existsByIdAndProjectMembersUserUsername(Long taskId, String username);

    @Query("""
    SELECT COUNT(t) > 0 FROM Task t
    JOIN t.project p
    JOIN p.members pm
    WHERE t.id = :taskId
    AND pm.user.username = :username
    AND pm.role = :role
    """)
    boolean existsLeaderByTask(Long taskId, String username, ProjectRole role);

    @Query(value = """
            SELECT 
            SUM(CASE WHEN status = 'TODO' THEN 1 ELSE 0 END) AS todo,
            SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgress,
            SUM(CASE WHEN status = 'DONE' THEN 1 ELSE 0 END) AS done,
            SUM(CASE WHEN status = 'PAUSED' THEN 1 ELSE 0 END) AS paused,
            SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled
            FROM task
    """, nativeQuery = true)
    TaskStatsProjection getTaskStatistics();

    @Query("""
        SELECT 
        COUNT(t.id) AS total,
        SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) AS todo,
        SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgress,
        SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS done,
        SUM(CASE WHEN t.status = 'PAUSED' THEN 1 ELSE 0 END) AS paused,
        SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled
        FROM Task t
        JOIN t.project p
        JOIN p.members pm
        WHERE pm.user.username = :username 
        AND pm.role = 'LEADER'
    """)
    TaskStatusStats getTeamTaskStatsByManager(@org.springframework.data.repository.query.Param("username") String username);

    @Query("""
        SELECT 
        COUNT(t.id) AS total,
        SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) AS todo,
        SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgress,
        SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS done,
        SUM(CASE WHEN t.status = 'PAUSED' THEN 1 ELSE 0 END) AS paused,
        SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled
        FROM Task t
        WHERE t.assignee.username = :username
    """)
    TaskStatusStats getMyTaskStats(@org.springframework.data.repository.query.Param("username") String username);

    @Query("""
        SELECT 
        COUNT(t.id) AS total,
        SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) AS todo,
        SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgress,
        SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS done,
        SUM(CASE WHEN t.status = 'PAUSED' THEN 1 ELSE 0 END) AS paused,
        SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled
        FROM Task t
        WHERE t.project.id = :projectId
    """)
    TaskStatusStats getProjectTaskStats(@org.springframework.data.repository.query.Param("projectId") Long projectId);
}
