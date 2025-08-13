package pl.sgorski.ticket_service.dto;

import java.sql.Timestamp;

public record TicketEntityResponse (
        Long id,
        String title,
        String description,
        String status,
        Long reporterId,
        Long assigneeId,
        Timestamp createdAt,
        Timestamp updatedAt
)
{ }
