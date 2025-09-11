package pl.sgorski.comment_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.comment_service.dto.CreateCommentRequest;
import pl.sgorski.comment_service.mapper.CommentMapper;
import pl.sgorski.comment_service.model.Comment;
import pl.sgorski.comment_service.service.CommentService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN') or @commentSecurity.isTicketCreator(#ticketId, authentication.name)")
    public ResponseEntity<?> addComment(
            @PathVariable Long ticketId,
            @RequestBody @Valid CreateCommentRequest createCommentRequest,
            Principal principal
    ) {
        UUID authorId = UUID.fromString(principal.getName());
        Comment comment = commentMapper.toComment(createCommentRequest, ticketId, authorId);
        return ResponseEntity.status(201)
                .body(commentMapper.toResponse(commentService.saveComment(comment)));
    }

    @PutMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isCommentAuthor(#id, authentication.name)")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }
}
