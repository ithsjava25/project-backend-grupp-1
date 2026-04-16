package org.group1.projectbackend.mapper;

import org.group1.projectbackend.dto.comment.CommentDto;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.comment.UpdateCommentDto;
import org.group1.projectbackend.entity.Comment;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser() != null ? comment.getUser().getId() : null,
                comment.getUser() != null ? comment.getUser().getUsername() : null,
                comment.getTicket() != null ? comment.getTicket().getId() : null,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    public Comment toEntity(CreateCommentDto dto, User user, SupportTicket ticket) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setTicket(ticket);

        return comment;
    }

    public void updateEntity(UpdateCommentDto dto, Comment comment) {
        if (dto == null || comment == null) {
            return;
        }
        comment.setContent(dto.getContent());
    }
}