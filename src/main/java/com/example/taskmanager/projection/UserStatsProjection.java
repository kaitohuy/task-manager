package com.example.taskmanager.projection;

public interface UserStatsProjection {
    Long getTotals();
    Long getAdmins();
    Long getManagers();
    Long getMembers();
}
