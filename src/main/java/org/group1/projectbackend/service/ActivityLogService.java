package org.group1.projectbackend.service;

import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.ActivityLog;
import org.group1.projectbackend.entity.Document;
import org.group1.projectbackend.entity.User;
import org.group1.projectbackend.mapper.ActivityLogMapper;
import org.group1.projectbackend.repository.ActivityLogRepository;
import org.group1.projectbackend.repository.DocumentRepository;
import org.group1.projectbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository,
                              ActivityLogMapper activityLogMapper,
                              UserRepository userRepository,
                              DocumentRepository documentRepository) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    public ActivityLogDto createActivityLog(CreateActivityLogDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        Document document = null;
        if (dto.getDocumentId() != null) {
            document = documentRepository.findById(dto.getDocumentId())
                    .orElseThrow(() -> new RuntimeException("Document not found with id: " + dto.getDocumentId()));
        }

        ActivityLog activityLog = activityLogMapper.toEntity(dto, user, document);
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
                .orElseThrow(() -> new RuntimeException("Activity log not found with id: " + id));

        return activityLogMapper.toDto(activityLog);
    }

    public List<ActivityLogDto> getActivityLogsByDocumentId(Long documentId, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by("createdAt").descending()
                : Sort.by("createdAt").ascending();

        return activityLogRepository.findByDocumentId(documentId, sort)
                .stream()
                .map(activityLogMapper::toDto)
                .toList();
    }

    public List<ActivityLogDto> getActivityLogsByUserId(Long userId, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by("createdAt").descending()
                : Sort.by("createdAt").ascending();

        return activityLogRepository.findByUserId(userId, sort)
                .stream()
                .map(activityLogMapper::toDto)
                .toList();
    }
}