package pl.sgorski.common.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record TicketDto(
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
