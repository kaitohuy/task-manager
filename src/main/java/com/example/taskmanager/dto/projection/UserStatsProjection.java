package com.example.taskmanager.dto.projection;

public interface UserStatsProjection {
    Long getTotals();
    Long getAdmins();
    Long getManagers();
    Long getMembers();
}
