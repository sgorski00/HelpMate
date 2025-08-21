package pl.sgorski.ticket_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TicketStatus {
    OPEN("Opened"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
