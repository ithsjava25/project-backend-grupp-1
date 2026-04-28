package org.group1.projectbackend.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.group1.projectbackend.dto.document.DocumentDownloadResponse;
import org.group1.projectbackend.dto.document.DocumentResponse;
import org.group1.projectbackend.exception.GlobalExceptionHandler;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@Import(GlobalExceptionHandler.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    private DocumentResponse documentResponse;

    @BeforeEach
    void setUp() {
        documentResponse = new DocumentResponse(
                100L,
                "setup-guide.pdf",
                "application/pdf",
                12L,
                10L,
                1L,
                "alice",
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUploadDocumentForTicket() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "setup-guide.pdf",
                "application/pdf",
                "file-content".getBytes()
        );

        when(documentService.uploadDocument(eq(10L), eq(1L), any())).thenReturn(documentResponse);

        mockMvc.perform(multipart("/api/tickets/10/documents")
                        .file(file)
                        .param("uploadedByUserId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.fileName").value("setup-guide.pdf"))
                .andExpect(jsonPath("$.contentType").value("application/pdf"))
                .andExpect(jsonPath("$.ticketId").value(10))
                .andExpect(jsonPath("$.uploadedById").value(1));
    }

    @Test
    void shouldReturnUnauthorizedWhenUploadingDocumentWithoutAuthentication() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "setup-guide.pdf",
                "application/pdf",
                "file-content".getBytes()
        );

        mockMvc.perform(multipart("/api/tickets/10/documents")
                        .file(file)
                        .param("uploadedByUserId", "1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUploadedFileIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        when(documentService.uploadDocument(eq(10L), eq(1L), any()))
                .thenThrow(new IllegalArgumentException("Uploaded file cannot be empty"));

        mockMvc.perform(multipart("/api/tickets/10/documents")
                        .file(file)
                        .param("uploadedByUserId", "1")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Uploaded file cannot be empty"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnInternalServerErrorWhenStorageFailsDuringUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "setup-guide.pdf",
                "application/pdf",
                "file-content".getBytes()
        );

        when(documentService.uploadDocument(eq(10L), eq(1L), any()))
                .thenThrow(new IllegalStateException("Failed to upload object to S3-compatible storage"));

        mockMvc.perform(multipart("/api/tickets/10/documents")
                        .file(file)
                        .param("uploadedByUserId", "1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListDocumentsForTicket() throws Exception {
        when(documentService.listDocumentsForTicket(10L)).thenReturn(List.of(documentResponse));

        mockMvc.perform(get("/api/tickets/10/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].fileName").value("setup-guide.pdf"))
                .andExpect(jsonPath("$[0].ticketId").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDownloadDocument() throws Exception {
        byte[] content = "file-content".getBytes();
        DocumentDownloadResponse downloadResponse = new DocumentDownloadResponse(
                "setup-guide.pdf",
                "application/pdf",
                new ByteArrayResource(content)
        );

        when(documentService.downloadDocument(100L)).thenReturn(downloadResponse);
        String expectedContentDisposition = ContentDisposition.builder("attachment")
                .filename("setup-guide.pdf", StandardCharsets.UTF_8)
                .build()
                .toString();

        mockMvc.perform(get("/api/documents/100/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, expectedContentDisposition))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(content));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteDocument() throws Exception {
        doNothing().when(documentService).deleteDocument(any(), eq(100L));

        mockMvc.perform(delete("/api/documents/100")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDocumentDoesNotExist() throws Exception {
        when(documentService.downloadDocument(999L))
                .thenThrow(new ResourceNotFoundException("Document not found with id: 999"));

        mockMvc.perform(get("/api/documents/999/download"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Document not found with id: 999"));
    }
}
