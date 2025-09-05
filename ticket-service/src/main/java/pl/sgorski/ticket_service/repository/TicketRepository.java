package pl.sgorski.ticket_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.ticket_service.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByAssigneeId(UUID assigneeId);

    Page<Ticket> findAllByReporterId(UUID reporterId, Pageable pageable);
}
