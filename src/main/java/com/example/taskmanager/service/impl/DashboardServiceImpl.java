package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.projection.TaskStatusStats;
import com.example.taskmanager.dto.projection.UserStatsProjection;
import com.example.taskmanager.dto.response.AdminDashboardStatsDTO;
import com.example.taskmanager.dto.projection.TaskStatsProjection;
import com.example.taskmanager.dto.response.ManagerDashboardStatsDTO;
import com.example.taskmanager.enums.Role;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    @Override
    public AdminDashboardStatsDTO getAdminStats() {
        UserStatsProjection userStats = userRepository.getUserStatisticsJPQL();
        TaskStatsProjection taskStats = taskRepository.getTaskStatistics();

        return AdminDashboardStatsDTO.builder()
                .totalUsers(userStats.getTotals() != null ? userStats.getTotals() : 0)
                .totalAdmins(userStats.getAdmins() != null ? userStats.getAdmins() : 0)
                .totalManagers(userStats.getManagers() != null ? userStats.getManagers() : 0)
                .totalMembers(userStats.getMembers() != null ? userStats.getMembers() : 0)
                .totalProjects(projectRepository.count())
                .tasksTodo(taskStats.getTodo() != null ? taskStats.getTodo() : 0)
                .tasksInProgress(taskStats.getInProgress() != null ? taskStats.getInProgress() : 0)
                .tasksDone(taskStats.getDone() != null ? taskStats.getDone() : 0)
                .tasksPaused(taskStats.getPaused() != null ? taskStats.getPaused() : 0)
                .tasksCanceled(taskStats.getCancelled() != null ? taskStats.getCancelled() : 0)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public ManagerDashboardStatsDTO getManagerStats(String username) {

        long totalProjects = projectRepository.countProjectsManagedByUser(username);

        TaskStatusStats teamStats = taskRepository.getTeamTaskStatsByManager(username);
        TaskStatusStats myStats = taskRepository.getMyTaskStats(username);

        return ManagerDashboardStatsDTO.builder()
                .totalManagedProjects(totalProjects)

                .totalTeamTasks(teamStats != null && teamStats.getTotal() != null ? teamStats.getTotal() : 0)
                .teamTasksTodo(teamStats != null && teamStats.getTodo() != null ? teamStats.getTodo() : 0)
                .teamTasksInProgress(teamStats != null && teamStats.getInProgress() != null ? teamStats.getInProgress() : 0)
                .teamTasksDone(teamStats != null && teamStats.getDone() != null ? teamStats.getDone() : 0)
                .teamTaskPaused(teamStats != null && teamStats.getPaused() != null ? teamStats.getPaused() : 0)
                .teamTaskCancelled(teamStats != null && teamStats.getCancelled() != null ? teamStats.getCancelled() : 0)

                .totalMyTasks(myStats != null && myStats.getTotal() != null ? myStats.getTotal() : 0)
                .myTasksTodo(myStats != null && myStats.getTodo() != null ? myStats.getTodo() : 0)
                .myTasksInProgress(myStats != null && myStats.getInProgress() != null ? myStats.getInProgress() : 0)
                .myTasksDone(myStats != null && myStats.getDone() != null ? myStats.getDone() : 0)
                .myTaskPaused(myStats != null && myStats.getPaused() != null ? myStats.getPaused() : 0)
                .myTaskCancelled(myStats != null && myStats.getCancelled() != null ? myStats.getCancelled() : 0)
                .build();
    }
}