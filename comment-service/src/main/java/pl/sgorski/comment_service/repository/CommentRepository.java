package pl.sgorski.comment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.comment_service.model.Comment;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Set<Comment> findByTicketId(Long ticketId);
}
