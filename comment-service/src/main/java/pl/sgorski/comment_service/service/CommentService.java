package pl.sgorski.comment_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.comment_service.model.Comment;
import pl.sgorski.comment_service.repository.CommentRepository;
import pl.sgorski.common.exception.CommentNotFoundException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Set<Comment> getCommentsByTicketId(Long ticketId) {
        return commentRepository.findByTicketId(ticketId);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("Comment with id " + id + " not found")
        );
    }

    public void deleteCommentById(Long id) {
        var comment = getCommentById(id);
        comment.delete();
        commentRepository.save(comment);
    }
}
