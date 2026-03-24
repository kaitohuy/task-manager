package com.example.taskmanager.dto.response;

import com.example.taskmanager.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeInfoDTO {
    private Long id;
    private String username;
    private String fullName;
    private String imageUrl;
    private Gender gender;
}