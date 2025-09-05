package pl.sgorski.comment_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String content;
}
