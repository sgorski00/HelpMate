package pl.sgorski.ticket_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.sgorski.common.exception.NotCompatibleRoleException;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.common.utils.AuthorityUtils;
import pl.sgorski.ticket_service.dto.CreateTicketRequest;
import pl.sgorski.ticket_service.dto.UpdateTicketRequest;
import pl.sgorski.common.exception.TicketNotFoundException;
import pl.sgorski.ticket_service.mapper.TicketMapper;
import pl.sgorski.ticket_service.model.Ticket;
import pl.sgorski.ticket_service.model.TicketStatus;
import pl.sgorski.ticket_service.repository.TicketRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class TicketService {

    private final UserClientService userClientService;
    private final TicketRepository ticketRepository;
    private final TicketMapper mapper;

    public Ticket createTicket(CreateTicketRequest ticket, String reporterId) {
        Ticket newTicket = mapper.toTicket(ticket);
        userClientService.getUserById(reporterId).blockOptional().ifPresentOrElse(
                u -> newTicket.setReporterId(u.id()),
                () -> {
                    throw new UserNotFoundException("User not found with id: " + reporterId);
                }
        );
        return ticketRepository.save(newTicket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
    }

    public Page<Ticket> getTicketsForCurrentUser(Authentication authentication, Pageable pageable) {
        if (AuthorityUtils.isAdmin(authentication) || AuthorityUtils.isTechnician(authentication)) {
            return ticketRepository.findAll(pageable);
        }

        var user = userClientService.getUserById(authentication.getName()).blockOptional()
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + authentication.getName()));
        return ticketRepository.findAllByReporterId(user.id(), pageable);
    }

    public Ticket assignTicketById(Long ticketId, String assigneeId) {
        Ticket ticket = getTicketById(ticketId);
        userClientService.getUserById(assigneeId).blockOptional().ifPresentOrElse(
                u -> {
                    log.debug("Assigning ticket with id: {} to user with id: {} that contains roles: {}", ticketId, assigneeId, u.roles());
                    if(!u.roles().contains("TECHNICIAN")) throw new NotCompatibleRoleException("User with id: " + assigneeId + " is not a technician.");
                    ticket.setStatus(TicketStatus.IN_PROGRESS);
                    ticket.setAssigneeId(u.id());
                },
                () -> {
                    throw new UserNotFoundException("User not found with id: " + assigneeId);
                }
        );
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicketById(Long ticketId, UpdateTicketRequest ticketRequest) {
        Ticket existingTicket = getTicketById(ticketId);
        existingTicket.update(ticketRequest);
        return ticketRepository.save(existingTicket);
    }

    public Ticket changeStatusById(Long ticketId, TicketStatus ticketStatus) {
        Ticket existingTicket = getTicketById(ticketId);
        existingTicket.setStatus(ticketStatus);
        return ticketRepository.save(existingTicket);
    }
}
