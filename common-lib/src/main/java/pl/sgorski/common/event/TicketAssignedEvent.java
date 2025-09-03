package pl.sgorski.common.event;

import java.sql.Timestamp;

public record TicketAssignedEvent(
        String ticketId,
        String title,
        String description,
        String reporterId,
        String assigneeId,
        Timestamp createdAt
) implements TicketEvent { }
