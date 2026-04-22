package org.group1.projectbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Document entity class.
 * Represents a document uploaded by a user.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "File name cannot be empty")
    @Size(max = 255, message = "File name cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String fileName;

    @NotBlank(message = "Content type cannot be empty")
    @Size(max = 255, message = "Content type cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @NotBlank(message = "Storage key cannot be empty")
    @Size(max = 500, message = "Storage key cannot exceed 500 characters")
    @Column(nullable = false, length = 500, unique = true)
    private String storageKey;

    @NotBlank(message = "Bucket cannot be empty")
    @Size(max = 255, message = "Bucket name cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String bucket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    /**
     * Automatically sets the creation and update dates before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Automatically sets the update date before the entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
