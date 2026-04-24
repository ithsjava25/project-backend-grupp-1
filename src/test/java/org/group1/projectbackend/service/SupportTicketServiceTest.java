package org.group1.projectbackend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.TicketPriority;
import org.group1.projectbackend.entity.enums.TicketStatus;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.group1.projectbackend.service.ActivityLogService;
import org.group1.projectbackend.service.impl.SupportTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    private User user;
    private SupportTicket ticket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");

        ticket = new SupportTicket();
        ticket.setId(10L);
        ticket.setTitle("VPN access issue");
        ticket.setDescription("Cannot connect to the company VPN from home.");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(user);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateTicket() {
        CreateTicketRequest request = new CreateTicketRequest(
                "VPN access issue",
                "Cannot connect to the company VPN from home.",
                TicketPriority.HIGH
        );

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);

        TicketResponse response = supportTicketService.createTicket("alice", request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("VPN access issue");
        assertThat(response.priority()).isEqualTo(TicketPriority.HIGH);
        assertThat(response.createdById()).isEqualTo(1L);
        verify(supportTicketRepository).save(any(SupportTicket.class));
        verify(activityLogService).createActivityLog(argThat(dto ->
                dto.getActivityType() == ActivityType.TICKET_CREATED
                        && "Ticket created".equals(dto.getDescription())
                        && dto.getUserId().equals(1L)
                        && dto.getSupportTicketId().equals(10L)
        ));
    }

    @Test
    void shouldGetTicketById() {
        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        TicketResponse response = supportTicketService.getTicketById(10L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.createdByUsername()).isEqualTo("alice");
    }

    @Test
    void shouldListTicketsForUser() {
        when(supportTicketRepository.findByCreatedById(1L)).thenReturn(List.of(ticket));

        List<TicketResponse> responses = supportTicketService.getTicketsForUser(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(10L);
    }

    @Test
    void shouldUpdateTicketStatus() {
        UpdateTicketStatusRequest request = new UpdateTicketStatusRequest(TicketStatus.RESOLVED);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(supportTicketRepository.save(ticket)).thenReturn(ticket);

        TicketResponse response = supportTicketService.updateStatus("alice", 10L, request);

        assertThat(response.status()).isEqualTo(TicketStatus.RESOLVED);
        verify(supportTicketRepository).save(ticket);
        verify(activityLogService).createActivityLog(argThat(dto ->
                dto.getActivityType() == ActivityType.TICKET_STATUS_CHANGED
                        && "Ticket status changed from OPEN to RESOLVED".equals(dto.getDescription())
                        && dto.getUserId().equals(1L)
                        && dto.getSupportTicketId().equals(10L)
        ));
    }

    @Test
    void shouldThrowWhenTicketIsNotFoundById() {
        when(supportTicketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supportTicketService.getTicketById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ticket not found with id: 999");
    }

    @Test
    void shouldThrowWhenUpdatingMissingTicket() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(supportTicketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supportTicketService.updateStatus("alice", 999L, new UpdateTicketStatusRequest(TicketStatus.CLOSED)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ticket not found with id: 999");
    }
}
