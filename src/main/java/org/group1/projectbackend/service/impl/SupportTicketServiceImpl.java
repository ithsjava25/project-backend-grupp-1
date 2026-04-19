package org.group1.projectbackend.service.impl;

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
    public TicketResponse createTicket(CreateTicketRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        SupportTicket ticket = SupportTicket.builder()
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .createdBy(user)
                .build();

        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        return SupportTicketMapper.toResponse(savedTicket);
    }

    @Override
    public TicketResponse updateStatus(Long ticketId, UpdateTicketStatusRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        ticket.setStatus(request.status());

        SupportTicket updatedTicket = supportTicketRepository.save(ticket);
        return SupportTicketMapper.toResponse(updatedTicket);
    }

    @Override
    public List<TicketResponse> getTicketsForUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return SupportTicketMapper.toResponseList(supportTicketRepository.findByCreatedById(userId));
    }
}
