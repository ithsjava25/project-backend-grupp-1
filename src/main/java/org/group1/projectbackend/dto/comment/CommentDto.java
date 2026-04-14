package org.group1.projectbackend.dto.comment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private String content;
    private Long userId;
    private String username;
    private Long supportTicketId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}