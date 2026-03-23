package com.example.taskmanager.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectDashboardStatsDTO {
    private long totalMembers;
    private long totalTasks;
    private long tasksTodo;
    private long tasksInProgress;
    private long tasksDone;
    private long tasksPaused;
    private long tasksCancelled;
}