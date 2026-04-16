package org.group1.projectbackend.mapper;

import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.ActivityLog;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogDto toDto(ActivityLog activityLog) {
        if (activityLog == null) {
            return null;
        }

        return new ActivityLogDto(
                activityLog.getId(),
                activityLog.getActivityType(),
                activityLog.getDescription(),
                activityLog.getUser() != null ? activityLog.getUser().getId() : null,
                activityLog.getSupportTicket() != null ? activityLog.getSupportTicket().getId() : null,
                activityLog.getCreatedAt()
        );
    }

    public ActivityLog toEntity(CreateActivityLogDto dto, User user, SupportTicket supportTicket) {
        if (dto == null) {
            return null;
        }

        return new ActivityLog(
                null,
                dto.getActivityType(),
                dto.getDescription(),
                user,
                supportTicket,
                null
        );
    }
}