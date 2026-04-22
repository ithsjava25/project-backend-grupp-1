package org.group1.projectbackend.mapper;

import java.util.List;
import org.group1.projectbackend.dto.document.DocumentResponse;
import org.group1.projectbackend.entity.Document;

public final class DocumentMapper {

    private DocumentMapper() {
    }

    public static DocumentResponse toResponse(Document document) {
        if (document == null) {
            return null;
        }

        Long ticketId = document.getTicket() != null ? document.getTicket().getId() : null;
        Long uploadedById = document.getUploadedBy() != null ? document.getUploadedBy().getId() : null;
        String uploadedByUsername = document.getUploadedBy() != null ? document.getUploadedBy().getUsername() : null;

        return new DocumentResponse(
                document.getId(),
                document.getFileName(),
                document.getContentType(),
                document.getFileSize(),
                ticketId,
                uploadedById,
                uploadedByUsername,
                document.getCreatedAt()
        );
    }

    public static List<DocumentResponse> toResponseList(List<Document> documents) {
        if (documents == null) {
            return List.of();
        }

        return documents.stream()
                .map(DocumentMapper::toResponse)
                .toList();
    }
}
