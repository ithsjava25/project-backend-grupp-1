package org.group1.projectbackend.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.group1.projectbackend.entity.Comment;
import org.group1.projectbackend.dto.comment.CommentDto;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.comment.UpdateCommentDto;
import org.group1.projectbackend.mapper.CommentMapper;
import org.group1.projectbackend.repository.CommentRepository;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;

@Service
public class CommentService {

    private final SupportTicketRepository supportTicketRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, SupportTicketRepository supportTicketRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
    }

    // Create comment
    public CommentDto createComment(CreateCommentDto dto) {
        String content = dto.getContent();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        Comment comment = commentMapper.toEntity(dto);

        comment.setTicket(supportTicketRepository.findById(dto.getSupportTicketId())
                        .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + dto.getSupportTicketId()))
        );

        comment.setUser(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()))
        );

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    // Get comments by ticketId
    public List<CommentDto> getCommentsBySupportTicketId(Long ticketId, String sortDirection) {
        supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + ticketId));

       Sort sort;

       if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
           sort = Sort.by("createdAt").descending();
       } else {
           sort = Sort.by("createdAt").ascending();
       }

        List<Comment> comments = commentRepository.findByTicket_Id(ticketId, sort);

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();

    }

    // Delete comment
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        commentRepository.delete(comment);
    }

    // Update comment
    public CommentDto updateComment(Long id, UpdateCommentDto dto) {
        String content = dto.getContent();
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        commentMapper.updateEntity(dto, existingComment);
        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toDto(updatedComment);
    }
}