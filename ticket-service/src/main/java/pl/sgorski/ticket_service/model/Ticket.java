package pl.sgorski.ticket_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.sgorski.common.exception.IllegalStatusChangeException;
import pl.sgorski.ticket_service.dto.UpdateTicketRequest;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(nullable = false)
    private UUID reporterId;

    @Column(nullable = false)
    private UUID assigneeId;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @PrePersist
    private void setStatusIfNull() {
        if (status == null) {
            setStatus(TicketStatus.OPEN);
        }
    }

    public void update(UpdateTicketRequest ticketRequest) {
        if(!isModifiable()) {
            throw new IllegalStatusChangeException("Cannot update ticket with status " + this.status);
        }
        this.title = ticketRequest.title();
        this.description = ticketRequest.description();
    }

    public void setStatus(TicketStatus status) {
        if(isModifiable()) {
            this.status = status;
        } else {
            throw new IllegalStatusChangeException("Cannot change ticket status from " + this.status + " to " + status);
        }
    }

    private boolean isModifiable() {
        return this.status == null || this.status.equals(TicketStatus.OPEN) || this.status.equals(TicketStatus.IN_PROGRESS);
    }
}
