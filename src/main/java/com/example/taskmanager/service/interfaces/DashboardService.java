package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.response.AdminDashboardStatsDTO;
import com.example.taskmanager.dto.response.ManagerDashboardStatsDTO;

public interface DashboardService {

    AdminDashboardStatsDTO getAdminStats();
    ManagerDashboardStatsDTO getManagerStats(String username);
    void broadcastAdminStats();
    void broadcastManagerStats(String username);
}
