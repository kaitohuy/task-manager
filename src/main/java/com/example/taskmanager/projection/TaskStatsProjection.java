package com.example.taskmanager.projection;

public interface TaskStatsProjection {
    Long getTodo();
    Long getInProgress();
    Long getDone();
    Long getPaused();
    Long getCancelled();
}
