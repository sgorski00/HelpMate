package pl.sgorski.comment_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.common.exception.UserNotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("commentSecurity")
@RequiredArgsConstructor
public class CommentSecurityService {

    private final CommentService commentService;
    private final UserClientService userClientService;
    private final TicketClientService ticketClientService;

    public boolean isCommentAuthor(Long commentId, UUID userId) {
        var comment = commentService.getCommentById(commentId);
        return userClientService.getUserById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + userId)))
                .blockOptional()
                .filter(user -> user.id().equals(comment.getAuthorId()))
                .isPresent();
    }

    public boolean isTicketCreator(Long ticketId, String userId) {
        UUID userUuid = UUID.fromString(userId);
        return ticketClientService.isTicketCreator(ticketId, userUuid)
                .blockOptional()
                .orElse(false);
    }
}
