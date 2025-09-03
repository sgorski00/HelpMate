package pl.sgorski.common.event;

import java.sql.Timestamp;

public record TicketCreatedEvent(
        String ticketId,
        String title,
        String description,
        String reporterId,
        Timestamp createdAt
) implements TicketEvent { }