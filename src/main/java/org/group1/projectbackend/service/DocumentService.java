package org.group1.projectbackend.service;

import java.util.List;
import org.group1.projectbackend.dto.document.DocumentDownloadResponse;
import org.group1.projectbackend.dto.document.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    DocumentResponse uploadDocument(Long ticketId, Long uploadedByUserId, MultipartFile file);

    List<DocumentResponse> getAllDocuments();

    List<DocumentResponse> listDocumentsForTicket(Long ticketId);

    DocumentDownloadResponse downloadDocument(Long documentId);

    void deleteDocument(Long documentId);
}
