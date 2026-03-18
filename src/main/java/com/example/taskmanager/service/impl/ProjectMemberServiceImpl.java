package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.BadRequestException;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.dto.request.AddMemberDTO;
import com.example.taskmanager.dto.response.ProjectMemberDTO;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.ProjectMember;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.ProjectMapper;
import com.example.taskmanager.mapper.ProjectMemberMapper;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.repository.ProjectMemberRepository;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberMapper projectMemberMapper;

    @Override
    public void addMember(Long projectId, AddMemberDTO request) {

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean exists = projectMemberRepository.existsByProjectIdAndUserUsername(projectId, user.getUsername());

        if (exists) {
            throw new BadRequestException("User already in project");
        }

        ProjectMember pm = new ProjectMember();
        pm.setProject(project);
        pm.setUser(user);
        pm.setRole(request.getRole());

        projectMemberRepository.save(pm);
    }

    @Override
    public void removeMember(Long projectId, Long userId) {
        ProjectMember pm = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found")
        );
        projectMemberRepository.delete(pm);
    }

    @Override
    public List<ProjectMemberDTO> getMembers(Long projectId) {

        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toDTO)
                .toList();
    }
}
