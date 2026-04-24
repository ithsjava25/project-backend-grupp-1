package org.group1.projectbackend.controller.web;

import java.security.Principal;
import org.group1.projectbackend.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DocumentViewController {

    private final DocumentService documentService;

    public DocumentViewController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public String listDocuments(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());
        return "documents/list";
    }

    @PostMapping("/documents/{documentId}/delete")
    public String deleteDocument(
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

        return "redirect:/documents";
    }
}
