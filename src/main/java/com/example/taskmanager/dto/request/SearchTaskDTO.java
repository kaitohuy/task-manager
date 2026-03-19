package com.example.taskmanager.dto.request;

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
public class SearchTaskDTO {

    private Long projectId;
    private String keyword;
    private TaskStatus status;
    private LocalDateTime deadlineFrom;
    private LocalDateTime deadlineTo;
}
