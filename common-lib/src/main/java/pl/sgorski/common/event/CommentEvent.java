package pl.sgorski.common.event;

import java.util.UUID;

public interface CommentEvent extends Event {
    Long id();
    Long ticketId();
    UUID authorId();
    String content();
}
