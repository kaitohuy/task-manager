package com.example.taskmanager.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardStatsDTO {

    private long totalUsers;
    private long totalAdmins;
    private long totalManagers;
    private long totalMembers;
    private long totalProjects;
    private long tasksTodo;
    private long tasksInProgress;
    private long tasksDone;
    private long tasksPaused;
    private long tasksCanceled;
}
