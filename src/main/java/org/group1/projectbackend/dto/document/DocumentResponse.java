package org.group1.projectbackend.dto.document;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String fileName,
        String contentType,
        Long fileSize,
        Long ticketId,
        Long uploadedById,
        String uploadedByUsername,
        LocalDateTime createdAt
) {
}
