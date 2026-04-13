package com.example.taskmanager.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateMeetingDTO {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @NotNull
    @Future(message = "Start time must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @NotNull
    @Future(message = "End time must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @NotNull(message = "Project id is required")
    private Long projectId;

    private List<Long> participantIds;
}