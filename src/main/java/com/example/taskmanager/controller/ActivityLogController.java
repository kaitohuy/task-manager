package com.example.taskmanager.controller;

import com.example.taskmanager.dto.response.ActivityLogDTO;
import com.example.taskmanager.enums.ActivityType;
import com.example.taskmanager.service.interfaces.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService logService;

    @GetMapping("/me")
    public ResponseEntity<Page<ActivityLogDTO>> getMyLogs(Pageable pageable) {
        return ResponseEntity.ok(logService.getMyLogs(pageable));
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<ActivityLogDTO>> getAllLogs(Pageable pageable) {
        return ResponseEntity.ok(logService.getAllLogs(pageable));
    }

    @GetMapping("/manager")
    public ResponseEntity<Page<ActivityLogDTO>> getManagerLogs(@RequestParam List<Long> projectIds, Pageable pageable) {
        return ResponseEntity.ok(logService.getManagerLogs(projectIds, pageable));
    }

    @GetMapping("/entity")
    public ResponseEntity<Page<ActivityLogDTO>> getByEntity(@RequestParam ActivityType type, @RequestParam Long entityId, Pageable pageable) {
        return ResponseEntity.ok(logService.getByEntity(type, entityId, pageable));
    }
}