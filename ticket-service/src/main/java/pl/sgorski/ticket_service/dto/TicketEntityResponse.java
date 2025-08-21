package pl.sgorski.ticket_service.dto;

import java.sql.Timestamp;

public record TicketEntityResponse (
        Long id,
        String title,
        String description,
        String status,
        String reporterId,
        String assigneeId,
        Timestamp createdAt,
        Timestamp updatedAt
)
{ }
