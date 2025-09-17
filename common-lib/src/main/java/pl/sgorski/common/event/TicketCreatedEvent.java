package pl.sgorski.common.event;

import java.sql.Timestamp;
import java.util.UUID;

public record TicketCreatedEvent(
        String ticketId,
        String title,
        String description,
        UUID reporterId,
        Timestamp createdAt
) implements TicketEvent { }