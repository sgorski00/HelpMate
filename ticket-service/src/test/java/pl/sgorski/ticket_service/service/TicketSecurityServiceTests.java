package pl.sgorski.ticket_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.common.exception.TicketNotFoundException;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.ticket_service.model.Ticket;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketSecurityServiceTests {

    @Mock
    private TicketService ticketService;

    @Mock
    private UserClientService userClientService;

    @InjectMocks
    private TicketSecurityService ticketSecurityService;

    @Test
    void shouldReturnTrue_TicketCreator() {
        String creatorSub = "user_sub";
        Ticket ticket = new Ticket();
        ticket.setReporterId(creatorSub);
        UserDto user = new UserDto(
                creatorSub,
                "test",
                "test",
                "test",
                "test",
                Set.of()
        );
        when(userClientService.getUserById(anyString())).thenReturn(Mono.just(user));
        when(ticketService.getTicketById(anyLong())).thenReturn(ticket);

        boolean res = ticketSecurityService.isTicketCreator(1L, creatorSub);

        assertTrue(res);
    }

    @Test
    void shouldReturnFalse_NotTicketCreator() {
        String creatorSub = "user_sub";
        String otherSub = "other_sub";
        Ticket ticket = new Ticket();
        ticket.setReporterId(creatorSub);
        UserDto user = new UserDto(
                otherSub,
                "test",
                "test",
                "test",
                "test",
                Set.of()
        );
        when(userClientService.getUserById(anyString())).thenReturn(Mono.just(user));
        when(ticketService.getTicketById(anyLong())).thenReturn(ticket);

        boolean res = ticketSecurityService.isTicketCreator(1L, otherSub);

        assertFalse(res);
    }

    @Test
    void shouldReturnFalse_TicketReporterIdNotSet() {
        String creatorSub = "user_sub";
        Ticket ticket = new Ticket();
        ticket.setReporterId(null);
        UserDto user = new UserDto(
                creatorSub,
                "test",
                "test",
                "test",
                "test",
                Set.of()
        );
        when(userClientService.getUserById(anyString())).thenReturn(Mono.just(user));
        when(ticketService.getTicketById(anyLong())).thenReturn(ticket);

        boolean res = ticketSecurityService.isTicketCreator(1L, creatorSub);

        assertFalse(res);
    }

    @Test
    void shouldThrow_TicketNotFound() {
        when(ticketService.getTicketById(anyLong())).thenThrow(new TicketNotFoundException("Ticket not found"));

        assertThrows(TicketNotFoundException.class, () -> ticketSecurityService.isTicketCreator(1L, "creator_sub"));
    }

    @Test
    void shouldThrow_UserNotFound() {
        String creatorSub = "user_sub";
        String otherSub = "other_sub";
        Ticket ticket = new Ticket();
        ticket.setReporterId(creatorSub);
        when(ticketService.getTicketById(anyLong())).thenReturn(ticket);
        when(userClientService.getUserById(anyString())).thenReturn(Mono.empty());

        assertThrows(UserNotFoundException.class, () -> ticketSecurityService.isTicketCreator(1L, otherSub));
    }
}
