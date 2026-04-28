package org.group1.projectbackend.controller.web;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.group1.projectbackend.service.ActivityLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ActivityLogViewController {

    private final ActivityLogService activityLogService;

    public ActivityLogViewController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/activitylogs/view")
    public String listActivityLogs(
            @RequestParam(required = false) ActivityType type,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String ticket,
            @RequestParam(required = false) LocalDate date,
            Model model
    ) {
        String normalizedUsername = normalizeFilterValue(username);
        String normalizedTicket = normalizeFilterValue(ticket);

        List<ActivityLogDto> activityLogs = activityLogService.getAllActivityLogs().stream()
                .sorted((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()))
                .filter(activityLog -> type == null || activityLog.getActivityType() == type)
                .filter(activityLog -> normalizedUsername == null || containsIgnoreCase(activityLog.getUsername(), normalizedUsername))
                .filter(activityLog -> normalizedTicket == null
                        || containsIgnoreCase(activityLog.getTicketTitle(), normalizedTicket)
                        || matchesTicketId(activityLog, normalizedTicket))
                .filter(activityLog -> date == null
                        || (activityLog.getCreatedAt() != null && activityLog.getCreatedAt().toLocalDate().isEqual(date)))
                .toList();

        model.addAttribute("activityLogs", activityLogs);
        model.addAttribute("activityTypes", Arrays.asList(ActivityType.values()));
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedUsername", username);
        model.addAttribute("selectedTicket", ticket);
        model.addAttribute("selectedDate", date);

        return "activitylogs/list";
    }

    private String normalizeFilterValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private boolean containsIgnoreCase(String source, String expected) {
        return source != null && source.toLowerCase().contains(expected.toLowerCase());
    }

    private boolean matchesTicketId(ActivityLogDto activityLog, String expected) {
        return activityLog.getSupportTicketId() != null
                && String.valueOf(activityLog.getSupportTicketId()).contains(expected);
    }
}
