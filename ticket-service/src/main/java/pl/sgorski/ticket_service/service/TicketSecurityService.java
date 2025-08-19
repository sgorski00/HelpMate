package pl.sgorski.ticket_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("ticketSecurity")
@RequiredArgsConstructor
public class TicketSecurityService {

    private final TicketService ticketService;
    private final UserClientService userClientService;

    public boolean isTicketCreator(Long ticketId, String sub) {
        var ticket = ticketService.getTicketById(ticketId);
        var user = userClientService.getUserById(sub);
        if(ticket == null || user == null) {
            return false;
        }
        return ticket.getReporterId().equals(user.id());
    }
}
