package com.example.taskmanager.dto.response;

import com.example.taskmanager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private Long projectId;
    private String projectName;
    private AssigneeInfoDTO assignee;
}