package org.group1.projectbackend.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.entity.enums.TicketPriority;
import org.group1.projectbackend.entity.enums.TicketStatus;
import org.group1.projectbackend.exception.GlobalExceptionHandler;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.service.SupportTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SupportTicketController.class)
@Import(GlobalExceptionHandler.class)
class SupportTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupportTicketService supportTicketService;

    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        ticketResponse = new TicketResponse(
                10L,
                "VPN access issue",
                "Cannot connect to the company VPN from home.",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                "alice"
        );
    }

    @Test
    @WithMockUser
    void shouldCreateTicket() throws Exception {
        when(supportTicketService.createTicket(any(), any())).thenReturn(ticketResponse);

        mockMvc.perform(post("/api/tickets")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "VPN access issue",
                                  "description": "Cannot connect to the company VPN from home.",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("VPN access issue"));
    }

    @Test
    @WithMockUser
    void shouldGetTicketById() throws Exception {
        when(supportTicketService.getTicketById(10L)).thenReturn(ticketResponse);

        mockMvc.perform(get("/api/tickets/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.createdByUsername").value("alice"));
    }

    @Test
    @WithMockUser
    void shouldListTicketsForUser() throws Exception {
        when(supportTicketService.getTicketsForUser(1L)).thenReturn(List.of(ticketResponse));

        mockMvc.perform(get("/api/tickets/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    @WithMockUser
    void shouldUpdateTicketStatus() throws Exception {
        TicketResponse updated = new TicketResponse(
                10L,
                "VPN access issue",
                "Cannot connect to the company VPN from home.",
                TicketStatus.RESOLVED,
                TicketPriority.HIGH,
                ticketResponse.createdAt(),
                ticketResponse.updatedAt(),
                1L,
                "alice"
        );

        when(supportTicketService.updateStatus(any(), any(), any())).thenReturn(updated);

        mockMvc.perform(put("/api/tickets/10/status")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "RESOLVED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenTicketDoesNotExist() throws Exception {
        when(supportTicketService.getTicketById(999L))
                .thenThrow(new ResourceNotFoundException("Ticket not found with id: 999"));

        mockMvc.perform(get("/api/tickets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket not found with id: 999"));
    }
}
