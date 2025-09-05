package pl.sgorski.ticket_service.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record TicketEntityResponse (
        Long id,
        String title,
        String description,
        String status,
        UUID reporterId,
        UUID assigneeId,
        Timestamp createdAt,
        Timestamp updatedAt
)
{ }
