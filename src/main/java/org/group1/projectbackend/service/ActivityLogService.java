package org.group1.projectbackend.service;

import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.ActivityLog;
import org.group1.projectbackend.entity.SupportTicket;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.exception.ResourceNotFoundException;
import org.group1.projectbackend.mapper.ActivityLogMapper;
import org.group1.projectbackend.repository.ActivityLogRepository;
import org.group1.projectbackend.repository.SupportTicketRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final UserRepository userRepository;
    private final SupportTicketRepository supportTicketRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository,
                              ActivityLogMapper activityLogMapper,
                              UserRepository userRepository,
                              SupportTicketRepository supportTicketRepository) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
        this.userRepository = userRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ActivityLogDto createActivityLog(CreateActivityLogDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        SupportTicket supportTicket = null;
        if (dto.getSupportTicketId() != null) {
            supportTicket = supportTicketRepository.findById(dto.getSupportTicketId())
                    .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found with id: " + dto.getSupportTicketId()));
        }

        ActivityLog activityLog = activityLogMapper.toEntity(dto, user, supportTicket);
        ActivityLog saved = activityLogRepository.save(activityLog);

        return activityLogMapper.toDto(saved);
    }

    public List<ActivityLogDto> getAllActivityLogs() {
        return activityLogRepository.findAll()
                .stream()
                .map(activityLogMapper::toDto)
                .toList();
    }

    public ActivityLogDto getActivityLogById(Long id) {
        ActivityLog activityLog = activityLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity log not found with id: " + id));

        return activityLogMapper.toDto(activityLog);
    }

    public List<ActivityLogDto> getActivityLogsBySupportTicketId(Long supportTicketId, String sortDirection) {
        if (!supportTicketRepository.existsById(supportTicketId)) {
            throw new ResourceNotFoundException("Support ticket not found with id: " + supportTicketId);
        }

        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by("createdAt").descending()
                : Sort.by("createdAt").ascending();

        return activityLogRepository.findBySupportTicketId(supportTicketId, sort)
                .stream()
                .map(activityLogMapper::toDto)
                .toList();
    }

    public List<ActivityLogDto> getActivityLogsByUserId(Long userId, String sortDirection) {
        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by("createdAt").descending()
                : Sort.by("createdAt").ascending();

        return activityLogRepository.findByUserId(userId, sort)
                .stream()
                .map(activityLogMapper::toDto)
                .toList();
    }
}
