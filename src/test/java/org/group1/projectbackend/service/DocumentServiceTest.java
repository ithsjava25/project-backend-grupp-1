package org.group1.projectbackend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.group1.projectbackend.config.S3StorageProperties;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.group1.projectbackend.dto.document.DocumentDownloadResponse;
import org.group1.projectbackend.dto.document.DocumentResponse;
import org.group1.projectbackend.entity.Document;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.TicketPriority;
import org.group1.projectbackend.entity.enums.TicketStatus;
import org.group1.projectbackend.repository.DocumentRepository;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.group1.projectbackend.service.ActivityLogService;
import org.group1.projectbackend.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InOrder;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private S3StorageProperties s3StorageProperties;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private User user;
    private SupportTicket ticket;
    private Document document;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ticket = new SupportTicket();
        ticket.setId(10L);
        ticket.setTitle("VPN access issue");
        ticket.setDescription("Cannot connect to the company VPN from home.");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(user);

        document = new Document();
        document.setId(100L);
        document.setFileName("guide.pdf");
        document.setContentType("application/pdf");
        document.setFileSize(7L);
        document.setStorageKey("tickets/10/test-guide.pdf");
        document.setBucket("project-documents");
        document.setUploadedBy(user);
        document.setTicket(ticket);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldUploadDocumentForTicket() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "guide.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(s3StorageProperties.getBucket()).thenReturn("project-documents");
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document savedDocument = invocation.getArgument(0);
            savedDocument.setId(100L);
            savedDocument.setCreatedAt(LocalDateTime.now());
            return savedDocument;
        });

        DocumentResponse response = documentService.uploadDocument(10L, 1L, file);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.fileName()).isEqualTo("guide.pdf");
        assertThat(response.contentType()).isEqualTo("application/pdf");
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.uploadedById()).isEqualTo(1L);
        verify(objectStorageService).upload(anyString(), anyString(), anyLong(), any());
        verify(activityLogService).createActivityLog(argThat(dto ->
                dto.getActivityType() == ActivityType.FILE_UPLOADED
                        && "File uploaded: guide.pdf".equals(dto.getDescription())
                        && dto.getUserId().equals(1L)
                        && dto.getSupportTicketId().equals(10L)
        ));
    }

    @Test
    void shouldDeleteUploadedObjectWhenDocumentSaveFails() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "guide.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(s3StorageProperties.getBucket()).thenReturn("project-documents");
        when(documentRepository.save(any(Document.class))).thenThrow(new IllegalStateException("Database save failed"));

        assertThatThrownBy(() -> documentService.uploadDocument(10L, 1L, file))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Database save failed");

        verify(objectStorageService).upload(anyString(), anyString(), anyLong(), any());
        verify(objectStorageService).delete(anyString());
    }

    @Test
    void shouldUploadDocumentEvenWhenActivityLogFails() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "guide.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(s3StorageProperties.getBucket()).thenReturn("project-documents");
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document savedDocument = invocation.getArgument(0);
            savedDocument.setId(100L);
            savedDocument.setCreatedAt(LocalDateTime.now());
            return savedDocument;
        });
        when(activityLogService.createActivityLog(any()))
                .thenThrow(new RuntimeException("Activity log failed"));

        DocumentResponse response = documentService.uploadDocument(10L, 1L, file);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void shouldListDocumentsForTicket() {
        when(supportTicketRepository.existsById(10L)).thenReturn(true);
        when(documentRepository.findByTicketIdOrderByCreatedAtAsc(10L)).thenReturn(List.of(document));

        List<DocumentResponse> response = documentService.listDocumentsForTicket(10L);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().id()).isEqualTo(100L);
        assertThat(response.getFirst().fileName()).isEqualTo("guide.pdf");
        verify(documentRepository).findByTicketIdOrderByCreatedAtAsc(10L);
    }

    @Test
    void shouldDownloadDocument() throws Exception {
        when(documentRepository.findById(100L)).thenReturn(Optional.of(document));
        when(objectStorageService.download("tickets/10/test-guide.pdf"))
                .thenReturn(new ByteArrayResource("content".getBytes()));

        DocumentDownloadResponse response = documentService.downloadDocument(100L);

        assertThat(response.fileName()).isEqualTo("guide.pdf");
        assertThat(response.contentType()).isEqualTo("application/pdf");
        assertThat(response.resource().getContentAsByteArray()).isEqualTo("content".getBytes());
        verify(objectStorageService).download("tickets/10/test-guide.pdf");
    }

    @Test
    void shouldDeleteDocument() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(documentRepository.findById(100L)).thenReturn(Optional.of(document));

        documentService.deleteDocument("testuser", 100L);

        InOrder inOrder = inOrder(documentRepository, objectStorageService);
        inOrder.verify(documentRepository).delete(document);
        inOrder.verify(objectStorageService).delete("tickets/10/test-guide.pdf");
        verify(activityLogService).createActivityLog(argThat(dto ->
                dto.getActivityType() == ActivityType.FILE_DELETED
                        && "File deleted: guide.pdf".equals(dto.getDescription())
                        && dto.getUserId().equals(1L)
                        && dto.getSupportTicketId().equals(10L)
        ));
    }

    @Test
    void shouldDeleteDocumentEvenWhenActivityLogFails() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(documentRepository.findById(100L)).thenReturn(Optional.of(document));
        when(activityLogService.createActivityLog(any()))
                .thenThrow(new RuntimeException("Activity log failed"));

        documentService.deleteDocument("testuser", 100L);

        verify(documentRepository).delete(document);
        verify(objectStorageService).delete("tickets/10/test-guide.pdf");
    }
}
