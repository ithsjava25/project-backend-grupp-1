package org.group1.projectbackend.controller;

import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.service.SupportTicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @PostMapping
    public SupportTicket createTicket(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String description
    ) {
        return supportTicketService.createTicket(userId, title, description);
    }

    @PutMapping("/{id}/status")
    public SupportTicket updateTicketStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return supportTicketService.updateStatus(id, status);
    }

    @GetMapping("/user/{userId}")
    public List<SupportTicket> getTicketsForUser(@PathVariable Long userId) {
        return supportTicketService.getTicketsForUser(userId);
    }
}
