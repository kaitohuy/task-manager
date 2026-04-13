package com.example.taskmanager.projection;

import com.example.taskmanager.enums.Gender;

public interface MemberAvatarProjection {
    Long getProjectId();
    String getUsername();
    String getImageUrl();
    Gender getGender();
}