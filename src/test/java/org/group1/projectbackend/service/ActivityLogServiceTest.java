package org.group1.projectbackend.service;

import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.ActivityLog;
import org.group1.projectbackend.entity.Document;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.group1.projectbackend.mapper.ActivityLogMapper;
import org.group1.projectbackend.repository.ActivityLogRepository;
import org.group1.projectbackend.repository.DocumentRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private ActivityLogMapper activityLogMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    private ActivityLog activityLog;
    private ActivityLogDto activityLogDto;
    private CreateActivityLogDto createActivityLogDto;
    private User user;
    private Document document;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        document = new Document();
        document.setId(10L);

        activityLog = new ActivityLog();
        activityLog.setId(100L);
        activityLog.setActivityType(ActivityType.CREATED);
        activityLog.setDescription("Test activity log");
        activityLog.setUser(user);
        activityLog.setDocument(document);
        activityLog.setCreatedAt(LocalDateTime.now());

        activityLogDto = new ActivityLogDto(
                100L,
                ActivityType.CREATED,
                "Test activity log",
                1L,
                10L,
                activityLog.getCreatedAt()
        );

        createActivityLogDto = new CreateActivityLogDto(
                ActivityType.CREATED,
                "Test activity log",
                1L,
                10L
        );
    }

    @Test
    void shouldCreateActivityLogWhenDtoIsValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(activityLogMapper.toEntity(createActivityLogDto, user, document)).thenReturn(activityLog);
        when(activityLogRepository.save(activityLog)).thenReturn(activityLog);
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        ActivityLogDto result = activityLogService.createActivityLog(createActivityLogDto);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Test activity log");
        assertThat(result.getActivityType()).isEqualTo(ActivityType.CREATED);
        verify(activityLogRepository).save(activityLog);
    }

    @Test
    void shouldCreateActivityLogWithoutDocumentWhenDocumentIdIsNull() {
        CreateActivityLogDto dtoWithoutDocument = new CreateActivityLogDto(
                ActivityType.CREATED,
                "Test activity log",
                1L,
                null
        );

        ActivityLog activityLogWithoutDocument = new ActivityLog();
        activityLogWithoutDocument.setId(101L);
        activityLogWithoutDocument.setActivityType(ActivityType.CREATED);
        activityLogWithoutDocument.setDescription("Test activity log");
        activityLogWithoutDocument.setUser(user);
        activityLogWithoutDocument.setDocument(null);
        activityLogWithoutDocument.setCreatedAt(LocalDateTime.now());

        ActivityLogDto activityLogDtoWithoutDocument = new ActivityLogDto(
                101L,
                ActivityType.CREATED,
                "Test activity log",
                1L,
                null,
                activityLogWithoutDocument.getCreatedAt()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(activityLogMapper.toEntity(dtoWithoutDocument, user, null)).thenReturn(activityLogWithoutDocument);
        when(activityLogRepository.save(activityLogWithoutDocument)).thenReturn(activityLogWithoutDocument);
        when(activityLogMapper.toDto(activityLogWithoutDocument)).thenReturn(activityLogDtoWithoutDocument);

        ActivityLogDto result = activityLogService.createActivityLog(dtoWithoutDocument);

        assertThat(result).isNotNull();
        assertThat(result.getDocumentId()).isNull();
        assertThat(result.getDescription()).isEqualTo("Test activity log");
        verify(activityLogRepository).save(activityLogWithoutDocument);
    }

    @Test
    void shouldReturnAllActivityLogs() {
        when(activityLogRepository.findAll()).thenReturn(List.of(activityLog));
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        List<ActivityLogDto> result = activityLogService.getAllActivityLogs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Test activity log");
        verify(activityLogRepository).findAll();
    }

    @Test
    void shouldReturnActivityLogById() {
        when(activityLogRepository.findById(100L)).thenReturn(Optional.of(activityLog));
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        ActivityLogDto result = activityLogService.getActivityLogById(100L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getDescription()).isEqualTo("Test activity log");
        verify(activityLogRepository).findById(100L);
    }

    @Test
    void shouldReturnActivityLogsByDocumentId() {
        when(activityLogRepository.findByDocumentId(eq(10L), any(Sort.class)))
                .thenReturn(List.of(activityLog));
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        List<ActivityLogDto> result =
                activityLogService.getActivityLogsByDocumentId(10L, "asc");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDocumentId()).isEqualTo(10L);
        assertThat(result.get(0).getDescription()).isEqualTo("Test activity log");
        verify(activityLogRepository).findByDocumentId(eq(10L), any(Sort.class));
    }

    @Test
    void shouldReturnActivityLogsByUserId() {
        when(activityLogRepository.findByUserId(eq(1L), any(Sort.class)))
                .thenReturn(List.of(activityLog));
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        List<ActivityLogDto> result =
                activityLogService.getActivityLogsByUserId(1L, "asc");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        assertThat(result.get(0).getDescription()).isEqualTo("Test activity log");
        verify(activityLogRepository).findByUserId(eq(1L), any(Sort.class));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.createActivityLog(createActivityLogDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 1");
    }

    @Test
    void shouldThrowExceptionWhenDocumentDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.createActivityLog(createActivityLogDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Document not found with id: 10");
    }

    @Test
    void shouldThrowExceptionWhenActivityLogDoesNotExist() {
        when(activityLogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.getActivityLogById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Activity log not found with id: 999");
    }
}