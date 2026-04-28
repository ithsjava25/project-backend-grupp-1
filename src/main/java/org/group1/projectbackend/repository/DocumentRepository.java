package org.group1.projectbackend.repository;

import java.util.List;
import org.group1.projectbackend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
