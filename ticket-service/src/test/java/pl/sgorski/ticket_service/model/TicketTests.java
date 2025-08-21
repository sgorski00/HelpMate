package pl.sgorski.ticket_service.model;

import org.junit.jupiter.api.Test;
import pl.sgorski.common.exception.IllegalStatusChangeException;
import pl.sgorski.ticket_service.dto.UpdateTicketRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TicketTests {

    @Test
    void shouldUpdateTicket_StatusNull() {
        Ticket ticket = new Ticket();
        ticket.setStatus(null);
        String newTitle = "New Title";
        String newDescription = "New Description";
        UpdateTicketRequest updateRequest = new UpdateTicketRequest(newTitle, newDescription);

        ticket.update(updateRequest);

        assertEquals(newTitle, ticket.getTitle());
        assertEquals(newDescription, ticket.getDescription());
    }

    @Test
    void shouldUpdateTicket_StatusOpened() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        String newTitle = "New Title";
        String newDescription = "New Description";
        UpdateTicketRequest updateRequest = new UpdateTicketRequest(newTitle, newDescription);

        ticket.update(updateRequest);

        assertEquals(newTitle, ticket.getTitle());
        assertEquals(newDescription, ticket.getDescription());
    }

    @Test
    void shouldUpdateTicket_StatusInProgress() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        String newTitle = "New Title";
        String newDescription = "New Description";
        UpdateTicketRequest updateRequest = new UpdateTicketRequest(newTitle, newDescription);

        ticket.update(updateRequest);

        assertEquals(newTitle, ticket.getTitle());
        assertEquals(newDescription, ticket.getDescription());
    }

    @Test
    void shouldNotUpdateTicket_NotModifiable() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.CANCELLED);
        String newTitle = "New Title";
        String newDescription = "New Description";
        UpdateTicketRequest updateRequest = new UpdateTicketRequest(newTitle, newDescription);

        assertThrows(IllegalStatusChangeException.class, () -> ticket.update(updateRequest));
    }

    @Test
    void shouldSetStatus_Modifiable() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        TicketStatus newStatus = TicketStatus.IN_PROGRESS;

        ticket.setStatus(newStatus);

        assertEquals(newStatus, ticket.getStatus());
    }

    @Test
    void shouldNotSetStatus_NotModifiable() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.RESOLVED);

        assertThrows(IllegalStatusChangeException.class, () -> ticket.setStatus(TicketStatus.OPEN));
    }
}
