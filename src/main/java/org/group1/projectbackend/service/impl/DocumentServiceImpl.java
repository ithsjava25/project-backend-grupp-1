package org.group1.projectbackend.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.group1.projectbackend.config.S3StorageProperties;
import org.group1.projectbackend.dto.document.DocumentDownloadResponse;
import org.group1.projectbackend.dto.document.DocumentResponse;
import org.group1.projectbackend.entity.Document;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.mapper.DocumentMapper;
import org.group1.projectbackend.repository.DocumentRepository;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.group1.projectbackend.service.DocumentService;
import org.group1.projectbackend.service.ObjectStorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final ObjectStorageService objectStorageService;
    private final S3StorageProperties s3StorageProperties;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            SupportTicketRepository supportTicketRepository,
            UserRepository userRepository,
            ObjectStorageService objectStorageService,
            S3StorageProperties s3StorageProperties
    ) {
        this.documentRepository = documentRepository;
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.objectStorageService = objectStorageService;
        this.s3StorageProperties = s3StorageProperties;
    }

    @Override
    @Transactional
    public DocumentResponse uploadDocument(Long ticketId, Long uploadedByUserId, MultipartFile file) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        User uploadedBy = userRepository.findById(uploadedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + uploadedByUserId));

        validateFile(file);

        String fileName = cleanFileName(file.getOriginalFilename());
        String contentType = resolveContentType(file);
        String storageKey = buildStorageKey(ticketId, fileName);

        try {
            objectStorageService.upload(storageKey, contentType, file.getSize(), file.getInputStream());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read uploaded file", ex);
        }

        Document document = Document.builder()
                .fileName(fileName)
                .contentType(contentType)
                .fileSize(file.getSize())
                .storageKey(storageKey)
                .bucket(s3StorageProperties.getBucket())
                .uploadedBy(uploadedBy)
                .ticket(ticket)
                .build();

        try {
            return DocumentMapper.toResponse(documentRepository.save(document));
        } catch (RuntimeException ex) {
            deleteUploadedObjectAfterFailedSave(storageKey, ex);
            throw ex;
        }
    }

    @Override
    public List<DocumentResponse> getAllDocuments() {
        return DocumentMapper.toResponseList(documentRepository.findAll());
    }

    @Override
    public List<DocumentResponse> listDocumentsForTicket(Long ticketId) {
        if (!supportTicketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }

        return DocumentMapper.toResponseList(documentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId));
    }

    @Override
    public DocumentDownloadResponse downloadDocument(Long documentId) {
        Document document = findDocumentById(documentId);
        Resource resource = objectStorageService.download(document.getStorageKey());

        return new DocumentDownloadResponse(
                document.getFileName(),
                document.getContentType(),
                resource
        );
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = findDocumentById(documentId);
        String storageKey = document.getStorageKey();

        documentRepository.delete(document);
        deleteObjectAfterCommit(storageKey);
    }

    private Document findDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
    }

    private String cleanFileName(String fileName) {
        String cleanedFileName = StringUtils.cleanPath(fileName != null ? fileName : "file");

        if (cleanedFileName.contains("..")) {
            throw new IllegalArgumentException("File name is invalid");
        }

        return StringUtils.hasText(cleanedFileName) ? cleanedFileName : "file";
    }

    private String resolveContentType(MultipartFile file) {
        return StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream";
    }

    private String buildStorageKey(Long ticketId, String fileName) {
        return "tickets/" + ticketId + "/" + UUID.randomUUID() + "-" + fileName;
    }

    private void deleteUploadedObjectAfterFailedSave(String storageKey, RuntimeException saveException) {
        try {
            objectStorageService.delete(storageKey);
        } catch (RuntimeException deleteException) {
            saveException.addSuppressed(deleteException);
        }
    }

    private void deleteObjectAfterCommit(String storageKey) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            objectStorageService.delete(storageKey);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                objectStorageService.delete(storageKey);
            }
        });
    }
}
