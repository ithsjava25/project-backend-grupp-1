package org.group1.projectbackend.repository;

import org.group1.projectbackend.entity.ActivityLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Returns all activity logs for a specific document, sorted by the given sort order
    List<ActivityLog> findByDocumentId(Long documentId, Sort sort);

    // Returns all activity logs for a specific user, sorted by the given sort order
    List<ActivityLog> findByUserId(Long userId, Sort sort);

}
