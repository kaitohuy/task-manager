package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.AddMemberDTO;
import com.example.taskmanager.dto.request.AddMembers;
import com.example.taskmanager.dto.response.ApiResponse;
import com.example.taskmanager.dto.response.ProjectMemberDTO;
import com.example.taskmanager.dto.response.UserDTO;
import com.example.taskmanager.enums.ProjectRole;
import com.example.taskmanager.service.interfaces.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'LEADER')")
    public ResponseEntity<ProjectMemberDTO> addMember(@PathVariable Long id, @RequestBody AddMemberDTO request) {
        ProjectMemberDTO member = projectMemberService.addMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'LEADER')")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        projectMemberService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'MEMBER')")
    public ResponseEntity<Page<ProjectMemberDTO>> getMembers(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(projectMemberService.getMembers(id, pageable));
    }

    @PutMapping("/{id}/members/{userId}/role")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'LEADER')")
    public ResponseEntity<ProjectMemberDTO> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam ProjectRole role) {
        return ResponseEntity.ok(projectMemberService.updateMemberRole(id, userId, role));
    }

    @GetMapping("/{id}/available-users")
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'PROJECT', 'LEADER')")
    public ResponseEntity<Page<UserDTO>> getAvailableUsers(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(projectMemberService.getAvailableUsersToAdd(id, pageable));
    }

    @PostMapping("/{projectId}/members/bulk")
    public ResponseEntity<ApiResponse> addMembersBulk(@PathVariable Long projectId, @RequestBody AddMembers request) {
        projectMemberService.addMembersBatch(projectId, request.getUserIds());
        return ResponseEntity.ok(new ApiResponse("Add members successfully!"));
    }
}
