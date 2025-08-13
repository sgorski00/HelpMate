package pl.sgorski.ticket_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.ticket_service.model.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByAssigneeId(Long assigneeId);

    List<Ticket> findAllByReporterId(Long reporterId);
}
