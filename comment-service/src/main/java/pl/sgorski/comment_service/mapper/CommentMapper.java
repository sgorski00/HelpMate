package pl.sgorski.comment_service.mapper;

import org.mapstruct.Mapper;
import pl.sgorski.comment_service.dto.CommentResponse;
import pl.sgorski.comment_service.dto.CreateCommentRequest;
import pl.sgorski.comment_service.model.Comment;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment(CreateCommentRequest createCommentRequest, Long ticketId, UUID authorId);

    CommentResponse toResponse(Comment comment);
}
