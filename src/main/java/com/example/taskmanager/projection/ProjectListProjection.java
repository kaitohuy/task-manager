package com.example.taskmanager.projection;

import java.time.LocalDateTime;

public interface ProjectListProjection {
    Long getId();
    String getName();
    String getDescription();
    LocalDateTime getCreatedAt();
    String getCreatedByUsername();
    Long getCreatedById();
    String getCreatedByAvatar();
}
