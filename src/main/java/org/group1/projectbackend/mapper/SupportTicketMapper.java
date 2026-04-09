package org.group1.projectbackend.mapper;

import java.util.List;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.entity.SupportTicket;

public final class SupportTicketMapper {

    private SupportTicketMapper() {
    }

    public static TicketResponse toResponse(SupportTicket ticket) {
        if (ticket == null) {
            return null;
        }

        Long createdById = ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null;
        String createdByUsername = ticket.getCreatedBy() != null ? ticket.getCreatedBy().getUsername() : null;

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                createdById,
                createdByUsername
        );
    }

    public static List<TicketResponse> toResponseList(List<SupportTicket> tickets) {
        return tickets.stream()
                .map(SupportTicketMapper::toResponse)
                .toList();
    }
}
