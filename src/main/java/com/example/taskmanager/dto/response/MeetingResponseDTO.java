package com.example.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MeetingResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String roomCode;
    private Long projectId;
    private UserDTO organizer;
    private List<UserDTO> participants;
}