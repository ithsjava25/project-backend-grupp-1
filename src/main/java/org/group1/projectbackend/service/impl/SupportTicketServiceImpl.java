package org.group1.projectbackend.service.impl;

import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.TicketStatus;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.group1.projectbackend.service.SupportTicketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, UserRepository userRepository) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SupportTicket createTicket(Long userId, String title, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = SupportTicket.builder()
                .title(title)
                .description(description)
                .createdBy(user)
                .build();

        return supportTicketRepository.save(ticket);
    }

    @Override
    public SupportTicket updateStatus(Long ticketId, String status) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        TicketStatus ticketStatus = TicketStatus.valueOf(status);
        ticket.setStatus(ticketStatus);

        return supportTicketRepository.save(ticket);
    }

    @Override
    public List<SupportTicket> getTicketsForUser(Long userId) {
        return supportTicketRepository.findByCreatedById(userId);
    }
}
