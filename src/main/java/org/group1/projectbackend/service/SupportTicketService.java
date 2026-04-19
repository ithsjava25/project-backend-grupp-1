package org.group1.projectbackend.service;

import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;

import java.util.List;

public interface SupportTicketService {

    TicketResponse createTicket(CreateTicketRequest request);

    TicketResponse updateStatus(Long ticketId, UpdateTicketStatusRequest request);

    List<TicketResponse> getTicketsForUser(Long userId);
}
