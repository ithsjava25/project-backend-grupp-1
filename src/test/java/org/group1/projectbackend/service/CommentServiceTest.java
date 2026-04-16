package org.group1.projectbackend.service;

import org.group1.projectbackend.dto.comment.CommentDto;
import org.group1.projectbackend.dto.comment.CreateCommentDto;
import org.group1.projectbackend.dto.comment.UpdateCommentDto;
import org.group1.projectbackend.entity.Comment;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.mapper.CommentMapper;
import org.group1.projectbackend.repository.CommentRepository;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDto commentDto;
    private CreateCommentDto createCommentDto;
    private UpdateCommentDto updateCommentDto;
    private User user;
    private SupportTicket ticket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Testname");

        ticket = new SupportTicket();
        ticket.setId(10L);

        comment = new Comment();
        comment.setId(100L);
        comment.setContent("Test comment");
        comment.setUser(user);
        comment.setTicket(ticket);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentDto = new CommentDto(
                100L,
                "Test comment",
                1L,
                "Testname",
                10L,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );

        createCommentDto = new CreateCommentDto(
                "Test comment",
                10L,
                1L
        );

        updateCommentDto = new UpdateCommentDto(
                "Updated comment"
        );
    }

    @Test
    void shouldCreateCommentWhenDtoIsValid() {
        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(createCommentDto, user, ticket)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(activityLogService.createActivityLog(any())).thenReturn(null);

        CommentDto result = commentService.createComment(createCommentDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test comment");
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldReturnCommentsByTicketId() {
        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(commentRepository.findByTicket_Id(eq(10L), any(Sort.class)))
                .thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        List<CommentDto> result =
                commentService.getCommentsBySupportTicketId(10L, "asc");

        assertThat(result).hasSize(1);
        verify(commentRepository).findByTicket_Id(eq(10L), any(Sort.class));
    }

    @Test
    void shouldDeleteCommentWhenCommentExists() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        when(activityLogService.createActivityLog(any())).thenReturn(null);

        commentService.deleteComment(100L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void shouldUpdateCommentWhenContentIsValid() {
        CommentDto updatedCommentDto = new CommentDto(
                100L,
                "Updated comment",
                1L,
                "Testname",
                10L,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(updatedCommentDto);
        when(activityLogService.createActivityLog(any())).thenReturn(null);

        CommentDto result =
                commentService.updateComment(100L, updateCommentDto);

        assertThat(result.getContent()).isEqualTo("Updated comment");
        verify(commentMapper).updateEntity(updateCommentDto, comment);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingComment() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Comment not found with id: 999");
    }
}