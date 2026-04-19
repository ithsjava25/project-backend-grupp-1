package org.group1.projectbackend.dto.activitylog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.group1.projectbackend.entity.enums.ActivityType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityLogDto {

    @NotNull
    private ActivityType activityType;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    private Long userId;

    private Long supportTicketId;

}