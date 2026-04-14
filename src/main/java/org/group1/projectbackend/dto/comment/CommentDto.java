package org.group1.projectbackend.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String content;
    private Long userId;
    private String username;
    private Long supportTicketId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}