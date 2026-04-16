package org.group1.projectbackend.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.security.Principal;
import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;
import org.group1.projectbackend.service.SupportTicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @PostMapping
    public TicketResponse createTicket(
            Principal principal,
            @Valid @RequestBody CreateTicketRequest request
    ) {
        if (principal == null) {
            throw new IllegalStateException("Authenticated principal is required");
        }

        return supportTicketService.createTicket(principal.getName(), request);
    }

    @GetMapping("/{id}")
    public TicketResponse getTicketById(@PathVariable Long id) {
        return supportTicketService.getTicketById(id);
    }

    @PutMapping("/{id}/status")
    public TicketResponse updateTicketStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {
        return supportTicketService.updateStatus(id, request);
    }

    @GetMapping("/user/{userId}")
    public List<TicketResponse> getTicketsForUser(@PathVariable Long userId) {
        return supportTicketService.getTicketsForUser(userId);
    }
}
