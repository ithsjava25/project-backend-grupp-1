package org.group1.projectbackend.controller.web;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Set;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.ticket.CreateTicketRequest;
import org.group1.projectbackend.dto.ticket.TicketResponse;
import org.group1.projectbackend.dto.ticket.UpdateTicketStatusRequest;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.TicketPriority;
import org.group1.projectbackend.entity.enums.TicketStatus;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.repository.UserRepository;
import org.group1.projectbackend.service.ActivityLogService;
import org.group1.projectbackend.service.CommentService;
import org.group1.projectbackend.service.DocumentService;
import org.group1.projectbackend.service.SupportTicketService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TicketViewController {

    private final SupportTicketService supportTicketService;
    private final ActivityLogService activityLogService;
    private final CommentService commentService;
    private final DocumentService documentService;
    private final UserRepository userRepository;

    public TicketViewController(
            SupportTicketService supportTicketService,
            ActivityLogService activityLogService,
            CommentService commentService,
            DocumentService documentService,
            UserRepository userRepository
    ) {
        this.supportTicketService = supportTicketService;
        this.activityLogService = activityLogService;
        this.commentService = commentService;
        this.documentService = documentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/tickets")
    public String listTickets(Model model) {
        model.addAttribute("tickets", supportTicketService.getAllTickets());
        return "tickets/list";
    }

    @GetMapping("/tickets/{id}")
    public String showTicket(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", supportTicketService.getTicketById(id));
        model.addAttribute("activityLogs", activityLogService.getActivityLogsBySupportTicketId(id, "desc"));
        model.addAttribute("comments", commentService.getCommentsBySupportTicketId(id, "asc"));
        model.addAttribute("documents", documentService.listDocumentsForTicket(id));
        model.addAttribute("statuses", TicketStatus.values());
        return "tickets/detail";
    }

    @GetMapping("/tickets/new")
    public String showCreateTicketForm(Model model) {
        model.addAttribute("createTicketRequest", new CreateTicketRequest("", "", null));
        model.addAttribute("priorities", TicketPriority.values());
        return "tickets/new";
    }

    @PostMapping("/tickets")
    public String createTicket(
            Principal principal,
            Authentication authentication,
            @Valid @ModelAttribute CreateTicketRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", TicketPriority.values());
            return "tickets/new";
        }

        TicketResponse ticket = supportTicketService.createTicket(principal.getName(), request);

        if (!hasPrivilegedAccess(authentication)) {
            redirectAttributes.addFlashAttribute("ticketSuccess", "Ticket skapades.");
            return "redirect:/";
        }

        return "redirect:/tickets/" + ticket.id();
    }

    @PostMapping("/tickets/{id}/comments")
    public String createComment(
            @PathVariable Long id,
            @RequestParam String content,
            Principal principal
    ) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + principal.getName()));

        CreateCommentDto comment = new CreateCommentDto();
        comment.setContent(content);
        comment.setSupportTicketId(id);
        comment.setUserId(user.getId());

        commentService.createComment(comment);

        return "redirect:/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/documents")
    public String uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + principal.getName()));

            documentService.uploadDocument(id, user.getId(), file);
            redirectAttributes.addFlashAttribute("documentSuccess", "Dokumentet laddades upp.");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("documentError", "Dokumentet kunde inte laddas upp: " + ex.getMessage());
        }

        return "redirect:/tickets/" + id;
    }

    @PostMapping("/tickets/{ticketId}/documents/{documentId}/delete")
    public String deleteDocument(
            @PathVariable Long ticketId,
            @PathVariable Long documentId,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            documentService.deleteDocument(principal.getName(), documentId);
            redirectAttributes.addFlashAttribute("documentSuccess", "Dokumentet togs bort.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("documentError", "Dokumentet kunde inte tas bort.");
        }

        return "redirect:/tickets/" + ticketId;
    }

    @PostMapping("/tickets/{id}/status")
    public String updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status,
            Principal principal
    ) {
        supportTicketService.updateStatus(principal.getName(), id, new UpdateTicketStatusRequest(status));

        return "redirect:/tickets/" + id;
    }

    private boolean hasPrivilegedAccess(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

        return authorities.contains("ROLE_ADMIN") || authorities.contains("ROLE_HANDLER");
    }
}
