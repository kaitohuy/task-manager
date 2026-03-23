package com.example.taskmanager.dto.response;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssignmentDTO {
    private Long id;
    private Long taskId;
    private UserDTO user;
    private UserDTO assignor;
    private LocalDateTime createdAt;
}
