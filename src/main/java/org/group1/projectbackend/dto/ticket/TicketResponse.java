package org.group1.projectbackend.dto.ticket;

import java.time.LocalDateTime;
import org.group1.projectbackend.entity.enums.TicketPriority;
import org.group1.projectbackend.entity.enums.TicketStatus;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdById,
        String createdByUsername
) {
}
