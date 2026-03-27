package com.example.taskmanager.projection;

public interface TaskStatusStats {
    Long getTotal();
    Long getTodo();
    Long getInProgress();
    Long getDone();
    Long getPaused();
    Long getCancelled();
}
