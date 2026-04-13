package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.request.CreateMeetingDTO;
import com.example.taskmanager.dto.request.UpdateMeetingDTO;
import com.example.taskmanager.dto.response.MeetingResponseDTO;
import com.example.taskmanager.entity.Meeting;
import com.example.taskmanager.entity.Notification;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.NotificationType;
import com.example.taskmanager.enums.Role;
import com.example.taskmanager.mapper.MeetingMapper;
import com.example.taskmanager.repository.MeetingRepository;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.MeetingService;
import com.example.taskmanager.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final MeetingMapper meetingMapper;

    @Override
    @Transactional
    public MeetingResponseDTO createMeeting(CreateMeetingDTO request, String organizerUsername) {
        Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User organizer = userRepository.findByUsername(organizerUsername).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<User> participants = userRepository.findAllById(request.getParticipantIds());

        // taskmgr-pro5-8f7a9c
        String uniqueRoomCode = "taskmgr-pro" + project.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .roomCode(uniqueRoomCode)
                .project(project)
                .organizer(organizer)
                .participants(participants)
                .build();

        Meeting savedMeeting = meetingRepository.save(meeting);

        participants.forEach(user -> {
            if (!user.getId().equals(organizer.getId())) {
                Notification notif = Notification.builder()
                        .recipient(user)
                        .sender(organizer)
                        .type(NotificationType.MEETING_INVITE)
                        .message(organizer.getUsername() + " has invited you join meeting : " + savedMeeting.getTitle())
                        .targetId(savedMeeting.getId())
                        .build();
                notificationService.sendNotification(notif);
            }
        });

        return meetingMapper.toDto(savedMeeting);
    }

    @Override
    public List<MeetingResponseDTO> getMeetingsByProject(Long projectId) {
        return meetingRepository.findByProjectId(projectId)
                .stream()
                .map(meetingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MeetingResponseDTO updateMeeting(Long id, UpdateMeetingDTO request, String username) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOrganizer = meeting.getOrganizer().getUsername().equals(username);
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);

        if (!isOrganizer && !isAdmin) {
            throw new RuntimeException("You don't have permission to update this meeting");
        }

        if (request.getTitle() != null) meeting.setTitle(request.getTitle());
        if (request.getDescription() != null) meeting.setDescription(request.getDescription());
        if (request.getStartTime() != null) meeting.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) meeting.setEndTime(request.getEndTime());
        if (request.getParticipantIds() != null) {
            List<User> participants = userRepository.findAllById(request.getParticipantIds());
            meeting.setParticipants(participants);
        }

        Meeting updated = meetingRepository.save(meeting);
        return meetingMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteMeeting(Long meetingId, String username) {
        // 1. Tìm Meeting
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOrganizer = meeting.getOrganizer().getUsername().equals(username);
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);

        if (!isOrganizer && !isAdmin) {
            throw new RuntimeException("You don't have permission to delete this meeting");
        }

        meeting.getParticipants().forEach(participant -> {
            if (!participant.getUsername().equals(username)) {
                Notification notif = Notification.builder()
                        .recipient(participant)
                        .sender(meeting.getOrganizer())
                        .type(NotificationType.SYSTEM_ALERT)
                        .message("The meeting '" + meeting.getTitle() + "' has been cancelled by " + user.getUsername())
                        .build();
                notificationService.sendNotification(notif);
            }
        });

        meetingRepository.delete(meeting);
    }
}