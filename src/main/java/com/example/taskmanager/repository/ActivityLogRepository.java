package com.example.taskmanager.repository;

import com.example.taskmanager.entity.ActivityLog;
import com.example.taskmanager.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByTypeAndEntityId(ActivityType type, Long entityId, Pageable pageable);

    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);

    Page<ActivityLog> findAll(Pageable pageable);

    Page<ActivityLog> findByProjectIdIn(List<Long> projectIds, Pageable pageable);
}