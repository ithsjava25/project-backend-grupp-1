package org.group1.projectbackend.controller;

import org.group1.projectbackend.dto.comment.CommentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private org.group1.projectbackend.service.CommentService commentService;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto(
                1L,
                "Test content",
                1L,
                "Testuser",
                10L,
                null,
                null
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateComment() throws Exception {
        when(commentService.createComment(any())).thenReturn(commentDto);

        mockMvc.perform(post("/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content":"Test content",
                                  "supportTicketId":10,
                                  "userId":1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCommentsBySupportTicketId() throws Exception {
        when(commentService.getCommentsBySupportTicketId(10L, "asc"))
                .thenReturn(List.of(commentDto));

        mockMvc.perform(get("/comments/ticket/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Test content"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateComment() throws Exception {
        CommentDto updatedComment = new CommentDto(
                1L,
                "Updated content",
                1L,
                "Testuser",
                10L,
                null,
                null
        );

        when(commentService.updateComment(any(Long.class), any())).thenReturn(updatedComment);

        mockMvc.perform(put("/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content":"Updated content"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/comments/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
