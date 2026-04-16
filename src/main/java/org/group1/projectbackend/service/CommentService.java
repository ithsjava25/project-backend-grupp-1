package org.group1.projectbackend.service;

import java.util.List;

import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.ActivityType;
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
    private final ActivityLogService activityLogService;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, SupportTicketRepository supportTicketRepository, UserRepository userRepository, ActivityLogService activityLogService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    // Create comment
    public CommentDto createComment(CreateCommentDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        SupportTicket ticket = supportTicketRepository.findById(dto.getSupportTicketId())
                .orElseThrow(() -> new RuntimeException("Support ticket not found with id: " + dto.getSupportTicketId()));

        Comment comment = commentMapper.toEntity(dto, user, ticket);
        Comment savedComment = commentRepository.save(comment);

        try {
            CreateActivityLogDto logDto = new CreateActivityLogDto(
                    ActivityType.COMMENT_CREATED,
                    "Comment created for ticket id: " + ticket.getId(),
                    user.getId(),
                    null
            );
            activityLogService.createActivityLog(logDto);
        } catch (Exception e) {
            System.err.println("Failed to create activity log: " + e.getMessage());
        }

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

        CreateActivityLogDto logDto = new CreateActivityLogDto(
                ActivityType.COMMENT_DELETED,
                "Comment deleted with id: " + comment.getId(),
                comment.getUser().getId(),
                null
        );

        activityLogService.createActivityLog(logDto);
    }

    // Update comment
    public CommentDto updateComment(Long id, UpdateCommentDto dto) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        commentMapper.updateEntity(dto, existingComment);
        Comment updatedComment = commentRepository.save(existingComment);

        CreateActivityLogDto logDto = new CreateActivityLogDto(
                ActivityType.COMMENT_UPDATED,
                "Comment updated with id: " + existingComment.getId(),
                existingComment.getUser().getId(),
                null
        );

        activityLogService.createActivityLog(logDto);

        return commentMapper.toDto(updatedComment);

    }
}