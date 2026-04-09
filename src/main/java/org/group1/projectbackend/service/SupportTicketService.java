package org.group1.projectbackend.service;

import org.group1.projectbackend.entity.SupportTicket;

import java.util.List;

public interface SupportTicketService {

    SupportTicket createTicket(Long userId, String title, String description);

    SupportTicket updateStatus(Long ticketId, String status);

    List<SupportTicket> getTicketsForUser(Long userId);
}
