package com.example.taskmanager.repository;

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

}
