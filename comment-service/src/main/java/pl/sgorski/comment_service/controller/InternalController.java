package pl.sgorski.comment_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.comment_service.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal")
public class InternalController {

    private final CommentService commentService;

    @GetMapping("/comments/{ticketId}")
    public ResponseEntity<?> getCommentForTicket(
            @PathVariable Long ticketId
    ) {
        //TODO: ticket-service can take comments from this endpoint
        return ResponseEntity.ok(commentService.getCommentsByTicketId(ticketId));
    }
}
