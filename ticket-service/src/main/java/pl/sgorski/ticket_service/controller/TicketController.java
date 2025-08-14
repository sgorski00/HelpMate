package pl.sgorski.ticket_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.ticket_service.dto.CreateTicketRequest;
import pl.sgorski.ticket_service.maper.TicketMapper;
import pl.sgorski.ticket_service.service.TicketService;
import pl.sgorski.ticket_service.service.UserClientService;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final UserClientService userClientService;

    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody CreateTicketRequest createTicketRequest) {
        var ticket = ticketService.createTicket(createTicketRequest);
        return ResponseEntity.ok(ticketMapper.toDto(ticket));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        //TODO: check id user can access this ticket (admin, technician - always can, user - only if he created this ticket [reportedId == userid])
        var ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticketMapper.toDto(ticket));
    }

    @GetMapping
    public ResponseEntity<?> getAllTickets() {
        //todo: implement this method - admins should see all tickets, technician - when his id == assigneeId, user - only his tickets
        return null;
    }
}
