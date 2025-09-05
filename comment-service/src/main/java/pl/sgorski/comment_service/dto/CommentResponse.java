package pl.sgorski.comment_service.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class CommentResponse {
    private Long id;
    private Long ticketId;
    private UUID authorId;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
