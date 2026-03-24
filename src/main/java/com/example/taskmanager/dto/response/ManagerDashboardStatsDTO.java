package com.example.taskmanager.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ManagerDashboardStatsDTO {
    private long totalManagedProjects;

    private long totalTeamTasks;
    private long teamTasksTodo;
    private long teamTasksInProgress;
    private long teamTasksDone;
    private long teamTaskPaused;
    private long teamTaskCancelled;

    private long totalMyTasks;
    private long myTasksTodo;
    private long myTasksInProgress;
    private long myTasksDone;
    private long myTaskPaused;
    private long myTaskCancelled;
}