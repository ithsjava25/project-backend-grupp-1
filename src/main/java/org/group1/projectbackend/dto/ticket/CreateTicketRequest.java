package org.group1.projectbackend.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.group1.projectbackend.entity.enums.TicketPriority;

public record CreateTicketRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Title cannot be empty")
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
        String title,

        @NotBlank(message = "Description cannot be empty")
        @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
        String description,

        @NotNull(message = "Priority is required")
        TicketPriority priority
) {
}
