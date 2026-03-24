package com.example.taskmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private UserDTO createdBy;
    private int memberCount;
    private List<MemberAvatarDTO> memberAvatars;
}
