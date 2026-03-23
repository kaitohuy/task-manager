package com.example.taskmanager.dto.request;

import com.example.taskmanager.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMemberDTO {
    private Long userId;
    private ProjectRole role;
}
