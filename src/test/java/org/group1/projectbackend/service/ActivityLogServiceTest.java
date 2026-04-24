package org.group1.projectbackend.service;

import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.ActivityLog;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.mapper.ActivityLogMapper;
import org.group1.projectbackend.repository.ActivityLogRepository;
import org.group1.projectbackend.repository.SupportTicketRepository;
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
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    private ActivityLog activityLog;
    private ActivityLogDto activityLogDto;
    private CreateActivityLogDto createActivityLogDto;
    private User user;
    private SupportTicket supportTicket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");

        supportTicket = new SupportTicket();
        supportTicket.setId(10L);
        supportTicket.setTitle("VPN access issue");

        activityLog = new ActivityLog();
        activityLog.setId(100L);
        activityLog.setActivityType(ActivityType.TICKET_CREATED);
        activityLog.setDescription("Test activity log");
        activityLog.setUser(user);
        activityLog.setSupportTicket(supportTicket);
        activityLog.setCreatedAt(LocalDateTime.now());

        activityLogDto = new ActivityLogDto(
                100L,
                1L,
                "alice",
                10L,
                "VPN access issue",
                ActivityType.TICKET_CREATED,
                "Test activity log",
                activityLog.getCreatedAt()
        );

        createActivityLogDto = new CreateActivityLogDto(
                ActivityType.TICKET_CREATED,
                "Test activity log",
                1L,
                10L
        );
    }

    @Test
    void shouldCreateActivityLogWhenDtoIsValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(supportTicketRepository.findById(10L)).thenReturn(Optional.of(supportTicket));
        when(activityLogMapper.toEntity(createActivityLogDto, user, supportTicket)).thenReturn(activityLog);
        when(activityLogRepository.save(activityLog)).thenReturn(activityLog);
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        ActivityLogDto result = activityLogService.createActivityLog(createActivityLogDto);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Test activity log");
        assertThat(result.getActivityType()).isEqualTo(ActivityType.TICKET_CREATED);
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getTicketTitle()).isEqualTo("VPN access issue");
        verify(activityLogRepository).save(activityLog);
    }

    @Test
    void shouldCreateActivityLogWithoutSupportTicketWhenSupportTicketIdIsNull() {
        CreateActivityLogDto dtoWithoutSupportTicket = new CreateActivityLogDto(
                ActivityType.TICKET_CREATED,
                "Test activity log",
                1L,
                null
        );

        ActivityLog activityLogWithoutSupportTicket = new ActivityLog();
        activityLogWithoutSupportTicket.setId(101L);
        activityLogWithoutSupportTicket.setActivityType(ActivityType.TICKET_CREATED);
        activityLogWithoutSupportTicket.setDescription("Test activity log");
        activityLogWithoutSupportTicket.setUser(user);
        activityLogWithoutSupportTicket.setSupportTicket(null);
        activityLogWithoutSupportTicket.setCreatedAt(LocalDateTime.now());

        ActivityLogDto activityLogDtoWithoutSupportTicket = new ActivityLogDto(
                101L,
                1L,
                "alice",
                null,
                null,
                ActivityType.TICKET_CREATED,
                "Test activity log",
                activityLogWithoutSupportTicket.getCreatedAt()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(activityLogMapper.toEntity(dtoWithoutSupportTicket, user, null)).thenReturn(activityLogWithoutSupportTicket);
        when(activityLogRepository.save(activityLogWithoutSupportTicket)).thenReturn(activityLogWithoutSupportTicket);
        when(activityLogMapper.toDto(activityLogWithoutSupportTicket)).thenReturn(activityLogDtoWithoutSupportTicket);

        ActivityLogDto result = activityLogService.createActivityLog(dtoWithoutSupportTicket);

        assertThat(result).isNotNull();
        assertThat(result.getSupportTicketId()).isNull();
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getTicketTitle()).isNull();
        assertThat(result.getDescription()).isEqualTo("Test activity log");
        verify(activityLogRepository).save(activityLogWithoutSupportTicket);
    }

    @Test
    void shouldReturnAllActivityLogs() {
        when(activityLogRepository.findAll()).thenReturn(List.of(activityLog));
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        List<ActivityLogDto> result = activityLogService.getAllActivityLogs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Test activity log");
        assertThat(result.get(0).getUsername()).isEqualTo("alice");
        assertThat(result.get(0).getTicketTitle()).isEqualTo("VPN access issue");
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
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getTicketTitle()).isEqualTo("VPN access issue");
        verify(activityLogRepository).findById(100L);
    }

    @Test
    void shouldReturnActivityLogsBySupportTicketId() {
        when(supportTicketRepository.existsById(10L)).thenReturn(true);
        when(activityLogRepository.findBySupportTicketId(eq(10L), any(Sort.class)))
                .thenReturn(List.of(activityLog));
        when(activityLogMapper.toDto(activityLog)).thenReturn(activityLogDto);

        List<ActivityLogDto> result =
                activityLogService.getActivityLogsBySupportTicketId(10L, "asc");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSupportTicketId()).isEqualTo(10L);
        assertThat(result.get(0).getDescription()).isEqualTo("Test activity log");
        assertThat(result.get(0).getTicketTitle()).isEqualTo("VPN access issue");
        verify(activityLogRepository).findBySupportTicketId(eq(10L), any(Sort.class));
    }

    @Test
    void shouldThrowExceptionWhenSupportTicketDoesNotExistForTicketHistory() {
        when(supportTicketRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> activityLogService.getActivityLogsBySupportTicketId(999L, "desc"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Support ticket not found with id: 999");
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
        assertThat(result.get(0).getUsername()).isEqualTo("alice");
        verify(activityLogRepository).findByUserId(eq(1L), any(Sort.class));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.createActivityLog(createActivityLogDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 1");
    }

    @Test
    void shouldThrowExceptionWhenSupportTicketDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(supportTicketRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.createActivityLog(createActivityLogDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Support ticket not found with id: 10");
    }

    @Test
    void shouldThrowExceptionWhenActivityLogDoesNotExist() {
        when(activityLogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.getActivityLogById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Activity log not found with id: 999");
    }
}
