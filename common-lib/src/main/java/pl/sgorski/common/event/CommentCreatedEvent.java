package pl.sgorski.common.event;

import java.sql.Timestamp;
import java.util.UUID;

public record CommentCreatedEvent(
        Long id,
        Long ticketId,
        UUID authorId,
        String content,
        Timestamp createdAt
) implements CommentEvent {}
