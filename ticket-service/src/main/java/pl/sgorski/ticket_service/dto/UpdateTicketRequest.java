package pl.sgorski.ticket_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTicketRequest (
        @NotBlank(message = "Title can't be empty") String title,
        @NotBlank(message = "Description can't be empty") String description
) { }
