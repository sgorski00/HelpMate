package pl.sgorski.common.event;

import java.sql.Timestamp;
import java.util.UUID;

public record TicketAssignedEvent(
        String ticketId,
        String title,
        String description,
        UUID reporterId,
        UUID assigneeId,
        Timestamp createdAt
) implements TicketEvent { }
