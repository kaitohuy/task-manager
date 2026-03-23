package com.example.taskmanager.repository;

import com.example.taskmanager.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTaskIdOrderByUploadedAtDesc(Long taskId);
    long countByTaskId(Long taskId);
}