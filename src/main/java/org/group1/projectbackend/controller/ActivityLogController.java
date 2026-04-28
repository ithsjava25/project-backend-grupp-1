package org.group1.projectbackend.controller;

import jakarta.validation.Valid;
import org.group1.projectbackend.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<ActivityLogDto>> createActivityLog(
            @Valid @RequestBody CreateActivityLogDto dto) {

        ActivityLogDto created = activityLogService.createActivityLog(dto);

        return ResponseEntity.status(201)
                .body(new ApiResponse<>("success", created));
    }

    // Get ALL logs
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogDto>>> getAllActivityLogs() {

        List<ActivityLogDto> logs = activityLogService.getAllActivityLogs();

        return ResponseEntity.ok(
                new ApiResponse<>("success", logs)
        );
    }

    // Get by ID
    @GetMapping("/{activityLogId}")
    public ResponseEntity<ApiResponse<ActivityLogDto>> getActivityLogById(
            @PathVariable Long activityLogId) {

        ActivityLogDto log = activityLogService.getActivityLogById(activityLogId);

        return ResponseEntity.ok(
                new ApiResponse<>("success", log)
        );
    }

    // Get by ticket
    @GetMapping("/ticket/{supportTicketId}")
    public ResponseEntity<ApiResponse<List<ActivityLogDto>>> getByTicket(
            @PathVariable Long supportTicketId,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        List<ActivityLogDto> logs =
                activityLogService.getActivityLogsBySupportTicketId(
                        supportTicketId, sortDirection);

        return ResponseEntity.ok(
                new ApiResponse<>("success", logs)
        );
    }

    // Get by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ActivityLogDto>>> getByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        List<ActivityLogDto> logs =
                activityLogService.getActivityLogsByUserId(
                        userId, sortDirection);

        return ResponseEntity.ok(
                new ApiResponse<>("success", logs)
        );
    }
}