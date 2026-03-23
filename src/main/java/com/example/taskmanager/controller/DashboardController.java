package com.example.taskmanager.controller;

import com.example.taskmanager.dto.response.AdminDashboardStatsDTO;
import com.example.taskmanager.dto.response.ManagerDashboardStatsDTO;
import com.example.taskmanager.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardStatsDTO> getAdminDashboardStats() {
        return ResponseEntity.ok(dashboardService.getAdminStats());
    }

    @GetMapping("/manager/stats")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ManagerDashboardStatsDTO> getManagerDashboardStats(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(dashboardService.getManagerStats(username));
    }
}