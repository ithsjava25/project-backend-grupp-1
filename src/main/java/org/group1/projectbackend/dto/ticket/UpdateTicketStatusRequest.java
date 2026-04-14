package org.group1.projectbackend.dto.ticket;

import jakarta.validation.constraints.NotNull;
import org.group1.projectbackend.entity.enums.TicketStatus;

public record UpdateTicketStatusRequest(
        @NotNull(message = "Status is required")
        TicketStatus status
) {
}
