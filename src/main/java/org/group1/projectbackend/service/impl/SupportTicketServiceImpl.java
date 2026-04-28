package org.group1.projectbackend.service.impl;

import java.util.List;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.mapper.SupportTicketMapper;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.group1.projectbackend.service.ActivityLogService;
import org.group1.projectbackend.service.SupportTicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketServiceImpl.class);

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public SupportTicketServiceImpl(
            SupportTicketRepository supportTicketRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService
    ) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
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
    public TicketResponse updateStatus(String username, Long ticketId, UpdateTicketStatusRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        var previousStatus = ticket.getStatus();
        ticket.setStatus(request.status());

        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        logActivitySafely(
                ActivityType.TICKET_STATUS_CHANGED,
                "Ticket status changed from " + previousStatus + " to " + savedTicket.getStatus(),
                user.getId(),
                savedTicket.getId()
        );

        return SupportTicketMapper.toResponse(savedTicket);
    }

    @Override
    public List<TicketResponse> getAllTickets() {
        return SupportTicketMapper.toResponseList(supportTicketRepository.findAll());
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

        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        logActivitySafely(
                ActivityType.TICKET_CREATED,
                "Ticket created",
                user.getId(),
                savedTicket.getId()
        );

        return SupportTicketMapper.toResponse(savedTicket);
    }

    private void logActivitySafely(ActivityType activityType, String description, Long userId, Long ticketId) {
        try {
            activityLogService.createActivityLog(new CreateActivityLogDto(
                    activityType,
                    description,
                    userId,
                    ticketId
            ));
        } catch (RuntimeException ex) {
            logger.error("Failed to create activity log for ticketId={} userId={} activityType={}",
                    ticketId,
                    userId,
                    activityType,
                    ex);
        }
    }
}
