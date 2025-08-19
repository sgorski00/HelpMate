package pl.sgorski.ticket_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.sgorski.common.utils.AuthorityUtils;
import pl.sgorski.ticket_service.dto.CreateTicketRequest;
import pl.sgorski.ticket_service.exception.TicketNotFoundException;
import pl.sgorski.ticket_service.maper.TicketMapper;
import pl.sgorski.ticket_service.model.Ticket;
import pl.sgorski.ticket_service.repository.TicketRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final UserClientService userClientService;
    private final TicketRepository ticketRepository;
    private final TicketMapper mapper;

    public Ticket createTicket(CreateTicketRequest ticket) {
        Ticket newTicket = mapper.toTicket(ticket);
        return ticketRepository.save(newTicket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
    }

    public List<Ticket> getAllAssignedTickets(String assigneeId) {
        return ticketRepository.findAllByAssigneeId(assigneeId);
    }

    public Page<Ticket> getTicketsForCurrentUser(Authentication authentication, Pageable pageable) {
        if (AuthorityUtils.isAdmin(authentication) || AuthorityUtils.isTechnician(authentication)) {
            return ticketRepository.findAll(pageable);
        }

        var user = userClientService.getUserById(authentication.getName());
        return ticketRepository.findAllByReporterId(user.id(), pageable);
    }
}
