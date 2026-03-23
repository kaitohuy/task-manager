package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.AddMemberDTO;
import com.example.taskmanager.dto.response.ProjectMemberDTO;

import java.util.List;

public interface ProjectMemberService {

    void addMember(Long projectId, AddMemberDTO request);
    void removeMember(Long projectId, Long userId);
    List<ProjectMemberDTO> getMembers(Long projectId);
}
