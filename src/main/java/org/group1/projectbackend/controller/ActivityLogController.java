package org.group1.projectbackend.controller;

import jakarta.validation.Valid;
import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
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

    // Create activity log
    @PostMapping
    public ResponseEntity<ActivityLogDto> createActivityLog(@Valid @RequestBody CreateActivityLogDto dto) {
        ActivityLogDto createdActivityLog = activityLogService.createActivityLog(dto);
        return ResponseEntity.status(201).body(createdActivityLog);
    }

    // Get all activity logs
    @GetMapping
    public ResponseEntity<List<ActivityLogDto>> getAllActivityLogs() {
        List<ActivityLogDto> activityLogs = activityLogService.getAllActivityLogs();
        return ResponseEntity.ok(activityLogs);
    }

    // Get activity log by id
    @GetMapping("/{activityLogId}")
    public ResponseEntity<ActivityLogDto> getActivityLogById(@PathVariable Long activityLogId) {
        ActivityLogDto activityLog = activityLogService.getActivityLogById(activityLogId);
        return ResponseEntity.ok(activityLog);
    }

    // Get activity logs by document id
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<ActivityLogDto>> getActivityLogsByDocumentId(
            @PathVariable Long documentId,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(
                activityLogService.getActivityLogsByDocumentId(documentId, sortDirection)
        );
    }

    // Get activity logs by user id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLogDto>> getActivityLogsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(
                activityLogService.getActivityLogsByUserId(userId, sortDirection)
        );
    }
}