package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateMeetingDTO;
import com.example.taskmanager.dto.request.UpdateMeetingDTO;
import com.example.taskmanager.dto.response.MeetingResponseDTO;

import java.util.List;

public interface MeetingService {
    MeetingResponseDTO createMeeting(CreateMeetingDTO request, String organizerUsername);
    List<MeetingResponseDTO> getMeetingsByProject(Long projectId);
    MeetingResponseDTO updateMeeting(Long id, UpdateMeetingDTO request, String username);
    void deleteMeeting(Long meetingId, String username);
}
