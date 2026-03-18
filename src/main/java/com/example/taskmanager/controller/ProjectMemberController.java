package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.AddMemberDTO;
import com.example.taskmanager.dto.response.ProjectMemberDTO;
import com.example.taskmanager.service.interfaces.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping("/{id}/members")
    @PreAuthorize("@projectSecurity.isLeader(#id, authentication)")
    public ResponseEntity<String> addMember(@PathVariable Long id, @RequestBody AddMemberDTO request) {
        projectMemberService.addMember(id, request);
        return ResponseEntity.ok("Member added");
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("@projectSecurity.isLeader(#id, authentication)")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        projectMemberService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("@projectSecurity.isMember(#id, authentication)")
    public ResponseEntity<List<ProjectMemberDTO>> getMembers(@PathVariable Long id) {
        return ResponseEntity.ok(projectMemberService.getMembers(id));
    }
}
