package org.group1.projectbackend.service.impl;

import java.util.List;
import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.mapper.SupportTicketMapper;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.group1.projectbackend.service.SupportTicketService;
import org.springframework.stereotype.Service;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, UserRepository userRepository) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TicketResponse createTicket(String username, CreateTicketRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return saveTicket(request, user);
    }

    @Override
    public TicketResponse getTicketById(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        return SupportTicketMapper.toResponse(ticket);
    }

    @Override
    public TicketResponse updateStatus(Long ticketId, UpdateTicketStatusRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        ticket.setStatus(request.status());

        return SupportTicketMapper.toResponse(supportTicketRepository.save(ticket));
    }

    @Override
    public List<TicketResponse> getTicketsForUser(Long userId) {
        return SupportTicketMapper.toResponseList(supportTicketRepository.findByCreatedById(userId));
    }

    private TicketResponse saveTicket(CreateTicketRequest request, User user) {
        SupportTicket ticket = SupportTicket.builder()
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .createdBy(user)
                .build();

        return SupportTicketMapper.toResponse(supportTicketRepository.save(ticket));
    }
}
