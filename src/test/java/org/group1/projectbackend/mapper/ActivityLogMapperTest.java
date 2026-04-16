package org.group1.projectbackend.mapper;

import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.ActivityLog;
import org.group1.projectbackend.entity.Document;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityLogMapperTest {

    private ActivityLogMapper activityLogMapper;

    @BeforeEach
    void setUp() {
        activityLogMapper = new ActivityLogMapper();
    }

    @Test
    void toDtoShouldReturnActivityLogDtoWhenActivityLogIsNotNull() {
        User user = new User();
        user.setId(1L);

        Document document = new Document();
        document.setId(10L);

        LocalDateTime now = LocalDateTime.now();

        ActivityLog activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setActivityType(ActivityType.CREATED);
        activityLog.setDescription("Test description");
        activityLog.setUser(user);
        activityLog.setDocument(document);
        activityLog.setCreatedAt(now);

        ActivityLogDto activityLogDto = activityLogMapper.toDto(activityLog);

        assertThat(activityLogDto).isNotNull();
        assertThat(activityLogDto.getId()).isEqualTo(1L);
        assertThat(activityLogDto.getActivityType()).isEqualTo(ActivityType.CREATED);
        assertThat(activityLogDto.getDescription()).isEqualTo("Test description");
        assertThat(activityLogDto.getUserId()).isEqualTo(1L);
        assertThat(activityLogDto.getDocumentId()).isEqualTo(10L);
        assertThat(activityLogDto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void toDtoShouldReturnNullWhenActivityLogIsNull() {
        ActivityLogDto result = activityLogMapper.toDto(null);

        assertThat(result).isNull();
    }

    @Test
    void toEntityShouldReturnActivityLogWhenDtoIsNotNull() {
        CreateActivityLogDto dto = new CreateActivityLogDto(
                ActivityType.CREATED,
                "Test description",
                1L,
                10L
        );

        User user = new User();
        user.setId(1L);

        Document document = new Document();
        document.setId(10L);

        ActivityLog activityLog = activityLogMapper.toEntity(dto, user, document);

        assertThat(activityLog).isNotNull();
        assertThat(activityLog.getActivityType()).isEqualTo(ActivityType.CREATED);
        assertThat(activityLog.getDescription()).isEqualTo("Test description");
        assertThat(activityLog.getUser()).isEqualTo(user);
        assertThat(activityLog.getDocument()).isEqualTo(document);
    }

    @Test
    void toEntityShouldReturnNullWhenDtoIsNull() {
        ActivityLog result = activityLogMapper.toEntity(null, null, null);

        assertThat(result).isNull();
    }

    @Test
    void toDtoShouldHandleNullUserAndDocument() {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setActivityType(ActivityType.CREATED);
        activityLog.setDescription("Test description");
        activityLog.setUser(null);
        activityLog.setDocument(null);
        activityLog.setCreatedAt(LocalDateTime.now());

        ActivityLogDto dto = activityLogMapper.toDto(activityLog);

        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getDocumentId()).isNull();
    }
}