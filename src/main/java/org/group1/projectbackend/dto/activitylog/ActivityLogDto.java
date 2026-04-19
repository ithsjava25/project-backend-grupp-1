package org.group1.projectbackend.dto.activitylog;

import lombok.*;
import org.group1.projectbackend.entity.enums.ActivityType;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {

    private Long id;
    private ActivityType activityType;
    private String description;
    private Long userId;
    private Long supportTicketId;
    private LocalDateTime createdAt;

}