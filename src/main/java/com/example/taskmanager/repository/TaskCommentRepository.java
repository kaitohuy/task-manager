package com.example.taskmanager.repository;

import com.example.taskmanager.entity.TaskComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    @Query("SELECT c FROM TaskComment c WHERE c.task.id = :taskId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    Page<TaskComment> findTopLevelCommentsByTaskId(@Param("taskId") Long taskId, Pageable pageable);

    Page<TaskComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentId, Pageable pageable);
    long countByTaskId(Long taskId);
}