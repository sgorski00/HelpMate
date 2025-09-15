package pl.sgorski.comment_service.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentTests {

    @Test
    void shouldDeleteComment() {
        var comment = new Comment();

        comment.delete();

        assertNotNull(comment.getDeletedAt());
    }

    @Test
    void shouldNotDeleteComment_AlreadyDeleted() {
        var comment = new Comment();
        comment.setDeletedAt(Timestamp.from(Instant.now()));

        assertThrows(IllegalStateException.class, comment::delete);
    }
}
