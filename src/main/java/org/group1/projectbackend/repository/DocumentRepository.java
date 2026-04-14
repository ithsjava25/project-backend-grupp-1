package org.group1.projectbackend.repository;

import org.group1.projectbackend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
