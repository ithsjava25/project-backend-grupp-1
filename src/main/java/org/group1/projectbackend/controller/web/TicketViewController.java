package org.group1.projectbackend.controller.web;

import java.security.Principal;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;
import org.group1.projectbackend.entity.enums.TicketPriority;
import org.group1.projectbackend.entity.enums.TicketStatus;
import org.group1.projectbackend.service.CommentService;
import org.group1.projectbackend.service.SupportTicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TicketViewController {

    private final SupportTicketService supportTicketService;
    private final CommentService commentService;

    public TicketViewController(SupportTicketService supportTicketService, CommentService commentService) {
        this.supportTicketService = supportTicketService;
        this.commentService = commentService;
    }

    @GetMapping("/tickets")
    public String listTickets(Model model) {
        model.addAttribute("tickets", supportTicketService.getAllTickets());
        return "tickets/list";
    }

    @GetMapping("/tickets/{id}")
    public String showTicket(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", supportTicketService.getTicketById(id));
        model.addAttribute("comments", commentService.getCommentsBySupportTicketId(id, "asc"));
        model.addAttribute("statuses", TicketStatus.values());
        return "tickets/detail";
    }

    @GetMapping("/tickets/new")
    public String showCreateTicketForm(Model model) {
        model.addAttribute("priorities", TicketPriority.values());
        return "tickets/new";
    }

    @PostMapping("/tickets")
    public String createTicket(
            Principal principal,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam TicketPriority priority
    ) {
        CreateTicketRequest request = new CreateTicketRequest(title, description, priority);
        TicketResponse ticket = supportTicketService.createTicket(principal.getName(), request);

        return "redirect:/tickets/" + ticket.id();
    }

    @PostMapping("/tickets/{id}/comments")
    public String createComment(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam Long userId
    ) {
        CreateCommentDto comment = new CreateCommentDto();
        comment.setContent(content);
        comment.setSupportTicketId(id);
        comment.setUserId(userId);

        commentService.createComment(comment);

        return "redirect:/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/status")
    public String updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status
    ) {
        supportTicketService.updateStatus(id, new UpdateTicketStatusRequest(status));

        return "redirect:/tickets/" + id;
    }
}
