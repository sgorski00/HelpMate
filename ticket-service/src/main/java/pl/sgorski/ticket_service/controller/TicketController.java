package pl.sgorski.ticket_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.ticket_service.dto.CreateTicketRequest;
import pl.sgorski.ticket_service.dto.UpdateTicketRequest;
import pl.sgorski.ticket_service.mapper.TicketMapper;
import pl.sgorski.ticket_service.model.TicketStatus;
import pl.sgorski.ticket_service.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @PostMapping
    public ResponseEntity<?> createTicket(
            @Valid @RequestBody CreateTicketRequest createTicketRequest,
            Authentication authentication
    ) {
        var loggedUserId = authentication.getName();
        var ticket = ticketService.createTicket(createTicketRequest, loggedUserId);
        return ResponseEntity.status(201).body(ticketMapper.toDto(ticket));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public ResponseEntity<?> assignTicket(
            @PathVariable Long id,
            @RequestParam(name = "assignee") String assigneeId
    ) {
        var ticket = ticketService.assignTicketById(id, assigneeId);
        return ResponseEntity.ok(ticketMapper.toDto(ticket));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public ResponseEntity<?> changeTicketStatus(
            @PathVariable Long id,
            @RequestParam(name = "status") TicketStatus ticketStatus
    ) {
        var ticket = ticketService.changeStatusById(id, ticketStatus);
        return ResponseEntity.ok(ticketMapper.toDto(ticket));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN') or @ticketSecurity.isTicketCreator(#id, authentication.name)")
    public ResponseEntity<?> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketRequest updateTicketRequest
    ) {
        var updatedTicket = ticketService.updateTicketById(id, updateTicketRequest);
        return ResponseEntity.ok(ticketMapper.toDto(updatedTicket));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN') or @ticketSecurity.isTicketCreator(#id, authentication.name)")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        var ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticketMapper.toDto(ticket));
    }

    @GetMapping
    public ResponseEntity<?> getAllTickets(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        var tickets = ticketService.getTicketsForCurrentUser(authentication, pageRequest);
        return ResponseEntity.ok(tickets.stream()
                .map(ticketMapper::toDto)
                .toList());
    }
}
