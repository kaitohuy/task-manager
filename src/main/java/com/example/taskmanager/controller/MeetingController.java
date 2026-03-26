package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateMeetingDTO;
import com.example.taskmanager.dto.request.UpdateMeetingDTO;
import com.example.taskmanager.dto.response.MeetingResponseDTO;
import com.example.taskmanager.service.impl.MeetingServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingServiceImpl meetingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isMember(#request.projectId, authentication)")
    public ResponseEntity<MeetingResponseDTO> createMeeting(@Valid @RequestBody CreateMeetingDTO request, Authentication authentication) {
        return ResponseEntity.ok(meetingService.createMeeting(request, authentication.getName()));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isMember(#projectId, authentication)")
    public ResponseEntity<List<MeetingResponseDTO>> getMeetingsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(meetingService.getMeetingsByProject(projectId));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeetingResponseDTO> updateMeeting(@PathVariable Long id, @RequestBody UpdateMeetingDTO request, Authentication authentication) {
        return ResponseEntity.ok(meetingService.updateMeeting(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id, Authentication authentication) {
        meetingService.deleteMeeting(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}