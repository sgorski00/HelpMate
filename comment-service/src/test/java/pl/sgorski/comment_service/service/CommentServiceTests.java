package pl.sgorski.comment_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.comment_service.mapper.CommentMapper;
import pl.sgorski.comment_service.model.Comment;
import pl.sgorski.comment_service.repository.CommentRepository;
import pl.sgorski.common.event.CommentCreatedEvent;
import pl.sgorski.common.exception.CommentNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private EventService eventService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void shouldSaveComment() {
        CommentCreatedEvent event = mock(CommentCreatedEvent.class);
        when(commentRepository.save(any())).thenReturn(new Comment());
        when(commentMapper.toCreatedEvent(any())).thenReturn(event);

        var result = commentService.saveComment(new Comment());

        assertNotNull(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(eventService, times(1)).publishCommentCreatedEvent(eq(event));
    }

    @Test
    void shouldReturnCommentsSetById() {
        when(commentRepository.findByTicketId(anyLong())).thenReturn(Set.of());

        var result = commentService.getCommentsByTicketId(1L);

        assertNotNull(result);
        verify(commentRepository, times(1)).findByTicketId(anyLong());
    }

    @Test
    void shouldReturnCommentById() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(new Comment()));

        var result = commentService.getCommentById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldNotReturnCommentById_ThrowNotFoundEx() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->commentService.getCommentById(1L));
    }

    @Test
    void shouldDeleteCommentById() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(new Comment()));

        commentService.deleteCommentById(1L);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void shouldNotDeleteCommentById_NotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteCommentById(1L));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldNotDeleteCommentById_AlreadyDeleted() {
        Comment comment = mock(Comment.class);
        doThrow(IllegalStateException.class).when(comment).delete();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertThrows(IllegalStateException.class, () -> commentService.deleteCommentById(1L));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}
