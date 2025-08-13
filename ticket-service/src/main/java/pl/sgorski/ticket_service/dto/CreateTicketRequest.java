package pl.sgorski.ticket_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
        @NotBlank(message = "Title cannot be empty") String title,
        @NotBlank(message = "Description cannot be empty") String description,
        @NotNull(message = "Reporter cannot be empty") Long reporterId,
        Long assigneeId
) {}
