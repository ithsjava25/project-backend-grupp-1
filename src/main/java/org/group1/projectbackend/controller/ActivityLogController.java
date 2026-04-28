package org.group1.projectbackend.controller;

import jakarta.validation.Valid;
import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activitylogs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @PostMapping
    public ResponseEntity<ActivityLogDto> createActivityLog(@Valid @RequestBody CreateActivityLogDto dto) {
        return ResponseEntity.status(201).body(activityLogService.createActivityLog(dto));
    }

    @GetMapping
    public ResponseEntity<List<ActivityLogDto>> getAllActivityLogs() {
        return ResponseEntity.ok(activityLogService.getAllActivityLogs());
    }

    @GetMapping("/{activityLogId}")
    public ResponseEntity<ActivityLogDto> getActivityLogById(@PathVariable Long activityLogId) {
        try {
            return ResponseEntity.ok(activityLogService.getActivityLogById(activityLogId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ticket/{supportTicketId}")
    public ResponseEntity<List<ActivityLogDto>> getByTicket(
            @PathVariable Long supportTicketId,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            List<ActivityLogDto> logs = activityLogService.getActivityLogsBySupportTicketId(supportTicketId, sortDirection);
            return ResponseEntity.ok(logs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLogDto>> getByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByUserId(userId, sortDirection));
    }
}