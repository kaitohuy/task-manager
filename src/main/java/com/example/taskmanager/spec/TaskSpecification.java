package com.example.taskmanager.spec;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TaskSpecification {

    public static Specification<Task> hasProjectId(Long projectId) {
        return (root, query, cb) -> {
            if (projectId == null) return null;
            return cb.equal(root.get("project").get("id"), projectId);
        };
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Task> titleContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<Task> fromDeadline(LocalDateTime fromDeadline) {
        return (root, query, cb) -> {
            if (fromDeadline == null) return null;
            return cb.greaterThanOrEqualTo(root.get("deadline"), fromDeadline);
        };
    }

    public static Specification<Task> toDeadline(LocalDateTime toDeadline) {
        return (root, query, cb) -> {
            if (toDeadline == null) return null;
            return cb.lessThanOrEqualTo(root.get("deadline"), toDeadline);
        };
    }
}