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
public class MemberAvatarDTO {
    private String userName;
    private String imageUrl;
    private Gender gender;
}
