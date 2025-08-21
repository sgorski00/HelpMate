package pl.sgorski.ticket_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.common.exception.UserNotFoundException;

@Service("ticketSecurity")
@RequiredArgsConstructor
public class TicketSecurityService {

    private final TicketService ticketService;
    private final UserClientService userClientService;

    public boolean isTicketCreator(Long ticketId, String userId) {
        var ticket = ticketService.getTicketById(ticketId);
        var user = userClientService.getUserById(userId).blockOptional().orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + userId)
        );
        return ticket.getReporterId() != null && ticket.getReporterId().equals(user.id());
    }
}
