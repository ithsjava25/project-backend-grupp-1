package org.group1.projectbackend.mapper;

import org.group1.projectbackend.dto.comment.CommentDto;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.comment.UpdateCommentDto;
import org.group1.projectbackend.entity.Comment;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() { commentMapper = new CommentMapper();}

    @Test
    void toDtoShouldReturnCommentDtoWhenCommentIsNotNull() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Test username");

        SupportTicket ticket = new SupportTicket();
        ticket.setId(10L);

        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test content");
        comment.setUser(user);
        comment.setTicket(ticket);
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        CommentDto commentDto = commentMapper.toDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getContent()).isEqualTo("Test content");
        assertThat(commentDto.getUserId()).isEqualTo(1L);
        assertThat(commentDto.getUsername()).isEqualTo("Test username");
        assertThat(commentDto.getSupportTicketId()).isEqualTo(10L);
        assertThat(commentDto.getCreatedAt()).isEqualTo(now);
        assertThat(commentDto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toEntityShouldReturnCommentWhenDtoIsNotNull() {
        CreateCommentDto dto = new CreateCommentDto("Test content", 10L, 1L);

        User user = new User();
        user.setId(1L);

        SupportTicket ticket = new SupportTicket();
        ticket.setId(10L);

        Comment comment = commentMapper.toEntity(dto, user, ticket);

        assertThat(comment).isNotNull();
        assertThat(comment.getContent()).isEqualTo("Test content");
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getTicket()).isEqualTo(ticket);
    }

    @Test
    void updateEntityShouldUpdateCommentWhenCommentAndDtoIsNotNull() {

        UpdateCommentDto dto = new UpdateCommentDto("Updated content");

        Comment comment = new Comment();
        comment.setContent("Old content");

        commentMapper.updateEntity(dto, comment);

        assertThat(comment.getContent()).isEqualTo("Updated content");
    }
}