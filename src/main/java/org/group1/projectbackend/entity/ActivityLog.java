package org.group1.projectbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ActivityLog entity class.
 * Records activities and actions performed by users within the application.
 */
@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Action description cannot be empty")
    @Size(max = 255, message = "Action description cannot exceed 255 characters")
    @Column(nullable = false)
    private String action;

    @NotBlank(message = "Entity type cannot be empty")
    @Size(max = 50, message = "Entity type cannot exceed 50 characters")
    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically sets the creation date before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
