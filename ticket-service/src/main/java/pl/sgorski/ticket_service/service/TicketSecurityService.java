package pl.sgorski.ticket_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.common.exception.UserNotFoundException;

@Service("ticketSecurity")
@RequiredArgsConstructor
public class TicketSecurityService {

    private final TicketService ticketService;
    private final UserClientService userClientService;

    public boolean isTicketCreator(Long ticketId, String sub) {
        var ticket = ticketService.getTicketById(ticketId);
        var user = userClientService.getUserById(sub).blockOptional().orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + sub)
        );
        return ticket.getReporterId().equals(user.id());
    }
}
