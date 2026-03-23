package com.example.taskmanager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskAssignmentDTO {

    @NotNull(message = "Task's ID is required")
    private Long taskId;

    @NotNull(message = "User's ID is required")
    private Long userId;

    private LocalDateTime createdAt;
}
