package org.group1.projectbackend.controller;

import jakarta.validation.Valid;
import org.group1.projectbackend.dto.comment.CommentDto;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.comment.UpdateCommentDto;
import org.group1.projectbackend.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Create comment
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CreateCommentDto dto) {
        CommentDto createdComment = commentService.createComment(dto);
        return ResponseEntity.status(201).body(createdComment);
    }

    // Get comments by ticketId
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentDto>> getCommentsBySupportTicketId(
            @PathVariable Long ticketId,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        List<CommentDto> comments =
                commentService.getCommentsBySupportTicketId(ticketId, sortDirection);

        return ResponseEntity.ok(comments);
    }

    // Update comment
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentDto dto
    ) {
        CommentDto updatedComment = commentService.updateComment(id, dto);
        return ResponseEntity.ok(updatedComment);
    }

    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}