package com.example.taskmanager.spec;

import com.example.taskmanager.dto.request.SearchTaskDTO;
import com.example.taskmanager.dto.response.TaskDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TaskSpecification {

    public static Specification<Task> hasProjectId(Long projectId) {
        return (root, query, cb) ->
                cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Task> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Task> fromDeadline(LocalDateTime fromDeadline) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("deadline"), fromDeadline);
    }

    public static Specification<Task> toDeadline(LocalDateTime toDeadline) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("deadline"), toDeadline);
    }
}
