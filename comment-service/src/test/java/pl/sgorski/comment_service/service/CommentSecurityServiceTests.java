package pl.sgorski.comment_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.comment_service.model.Comment;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.common.exception.UserNotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentSecurityServiceTests {

    @Mock
    private CommentService commentService;

    @Mock
    private UserClientService userClientService;

    @Mock
    private TicketClientService ticketClientService;

    @InjectMocks
    private CommentSecurityService securityService;

    private UUID authorId;
    private Comment comment;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        comment = new Comment();
        comment.setAuthorId(authorId);
    }

    @Test
    void isCommentAuthor_True() {
        UserDto user = mock(UserDto.class);
        when(user.id()).thenReturn(authorId);
        when(commentService.getCommentById(anyLong())).thenReturn(comment);
        when(userClientService.getUserById(any())).thenReturn(Mono.just(user));

        var result = securityService.isCommentAuthor(1L, authorId);

        assertTrue(result);
    }

    @Test
    void isCommentAuthor_False_NotAuthor() {
        UserDto user = mock(UserDto.class);
        when(user.id()).thenReturn(UUID.randomUUID());
        when(commentService.getCommentById(anyLong())).thenReturn(comment);
        when(userClientService.getUserById(any())).thenReturn(Mono.just(user));

        var result = securityService.isCommentAuthor(1L, authorId);

        assertFalse(result);
    }

    @Test
    void isCommentAuthor_Throw_UserNotFound() {
        when(commentService.getCommentById(anyLong())).thenReturn(comment);
        when(userClientService.getUserById(any())).thenReturn(Mono.empty());

        assertThrows(UserNotFoundException.class,() ->securityService.isCommentAuthor(1L, authorId));
    }

    @Test
    void isTicketCreator_True() {
        when(ticketClientService.isTicketCreator(anyLong(), any())).thenReturn(Mono.just(true));

        var result = securityService.isTicketCreator(1L, UUID.randomUUID().toString());

        assertTrue(result);
    }

    @Test
    void isTicketCreator_False_NotTicketCreator() {
        when(ticketClientService.isTicketCreator(anyLong(), any())).thenReturn(Mono.just(false));

        var result = securityService.isTicketCreator(1L, UUID.randomUUID().toString());

        assertFalse(result);
    }

    @Test
    void isTicketCreator_False_EmptyResponse() {
        when(ticketClientService.isTicketCreator(anyLong(), any())).thenReturn(Mono.empty());

        var result = securityService.isTicketCreator(1L, UUID.randomUUID().toString());

        assertFalse(result);
    }

}
