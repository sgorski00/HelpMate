package pl.sgorski.ticket_service.model;

import org.junit.jupiter.api.Test;
import pl.sgorski.common.exception.IllegalStatusChangeException;
import pl.sgorski.ticket_service.dto.UpdateTicketRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void isCreator_ShouldReturnTrue() {
        UUID creatorId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setReporterId(creatorId);

        boolean result = ticket.isCreator(creatorId);

        assertTrue(result);
    }

    @Test
    void isCreator_ShouldReturnFalse() {
        UUID creatorId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setReporterId(creatorId);

        boolean result = ticket.isCreator(UUID.randomUUID());

        assertFalse(result);
    }
}
