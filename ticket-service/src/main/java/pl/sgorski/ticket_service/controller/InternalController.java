package pl.sgorski.ticket_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.ticket_service.mapper.TicketMapper;
import pl.sgorski.ticket_service.service.TicketService;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<?> getTicketById(@PathVariable Long ticketId) {
        var ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticketMapper.toDto(ticket));
    }

    @GetMapping("/tickets/{ticketId}/is-creator/{userId}")
    public ResponseEntity<?> isTicketCreator(
            @PathVariable Long ticketId,
            @PathVariable UUID userId
    ) {
        var isCreator = ticketService.isTicketCreator(ticketId, userId);
        return ResponseEntity.ok(isCreator);
    }
}
