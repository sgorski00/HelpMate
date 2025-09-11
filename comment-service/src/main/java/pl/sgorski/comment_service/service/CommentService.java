package pl.sgorski.comment_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sgorski.comment_service.mapper.CommentMapper;
import pl.sgorski.comment_service.model.Comment;
import pl.sgorski.comment_service.repository.CommentRepository;
import pl.sgorski.common.exception.CommentNotFoundException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper mapper;
    private final EventService eventService;

    @Transactional
    public Comment saveComment(Comment comment) {
        Comment saved = commentRepository.save(comment);
        eventService.publishCommentCreatedEvent(mapper.toCreatedEvent(saved));
        return saved;
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
