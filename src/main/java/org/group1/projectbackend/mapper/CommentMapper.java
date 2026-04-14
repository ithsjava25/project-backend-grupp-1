package org.group1.projectbackend.mapper;

import org.group1.projectbackend.dto.comment.CommentDto;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.comment.UpdateCommentDto;
import org.group1.projectbackend.entity.Comment;
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
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getTicket().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    public Comment toEntity(CreateCommentDto dto) {
        if (dto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setContent(dto.getContent());

        return comment;
    }

    public void updateEntity(UpdateCommentDto dto, Comment comment) {
        if (dto == null || comment == null) {
            return;
        }
        comment.setContent(dto.getContent());
    }
}