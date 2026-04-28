package org.group1.projectbackend.service;

import java.util.List;
import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;

public interface SupportTicketService {

    TicketResponse createTicket(String username, CreateTicketRequest request);

    TicketResponse getTicketById(Long ticketId);

    TicketResponse updateStatus(Long ticketId, UpdateTicketStatusRequest request);

    List<TicketResponse> getAllTickets();

    List<TicketResponse> getTicketsForUser(Long userId);
}
