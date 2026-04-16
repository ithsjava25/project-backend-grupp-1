package org.group1.projectbackend.repository;

import org.group1.projectbackend.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Returns all comments for a specific support ticket, sorted by the given sort order
    List<Comment> findByTicket_Id(Long ticketId, Sort sort);

}
