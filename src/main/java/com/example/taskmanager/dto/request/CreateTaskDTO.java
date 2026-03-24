package com.example.taskmanager.dto.request;

import com.example.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskDTO {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Size(max = 500, message = "Description too long")
    private String description;

    @NotNull(message = "Status cannot be null")
    private TaskStatus status;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    @NotNull(message = "Project id required")
    private Long projectId;

    private Long assigneeId;
}
