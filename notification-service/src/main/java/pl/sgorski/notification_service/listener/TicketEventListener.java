package pl.sgorski.notification_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.TicketCreatedEvent;

@Service
@Log4j2
@RequiredArgsConstructor
public class TicketEventListener {

    private final Queue ticketCreatedQueue;

    @RabbitListener(queues = "#{ticketCreatedQueue.name}")
    public void handleTicketCreatedEvent(TicketCreatedEvent event) {
        log.info("Received ticket created event: {}", event);
    }
}
