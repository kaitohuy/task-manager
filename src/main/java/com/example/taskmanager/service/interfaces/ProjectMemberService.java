package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.AddMemberDTO;
import com.example.taskmanager.dto.response.ProjectMemberDTO;
import com.example.taskmanager.dto.response.UserDTO;
import com.example.taskmanager.enums.ProjectRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectMemberService {

    ProjectMemberDTO addMember(Long projectId, AddMemberDTO request);
    void removeMember(Long projectId, Long userId);
    Page<ProjectMemberDTO> getMembers(Long projectId, Pageable pageable);
    ProjectMemberDTO updateMemberRole(Long projectId, Long userId, ProjectRole newRole);
    Page<UserDTO> getAvailableUsersToAdd(Long projectId, Pageable pageable);
}
