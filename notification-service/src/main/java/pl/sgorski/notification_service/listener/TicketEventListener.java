package pl.sgorski.notification_service.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.notification_service.service.MailService;
import pl.sgorski.notification_service.service.NotificationProcessor;

import java.util.UUID;

@Service
@Log4j2
public class TicketEventListener {

    private final Queue ticketCreatedQueue;
    private final Queue ticketAssignedQueue;
    private final NotificationProcessor notificationProcessor;
    private final MailService mailService;

    public TicketEventListener(
            @Qualifier("ticketCreatedQueue") Queue ticketCreatedQueue,
            @Qualifier("ticketAssignedQueue") Queue ticketAssignedQueue,
            NotificationProcessor notificationProcessor, MailService mailService
    ) {
        this.ticketCreatedQueue = ticketCreatedQueue;
        this.ticketAssignedQueue = ticketAssignedQueue;
        this.notificationProcessor = notificationProcessor;
        this.mailService = mailService;
    }

    @RabbitListener(queues = "#{ticketCreatedQueue.name}")
    public void handleTicketCreatedEvent(
            TicketCreatedEvent payload,
            @Header("id") UUID eventId,
            @Header("eventType") String eventType
    ) {
        log.info("Received ticket created message: id={}, payload={}", eventId, payload);
        notificationProcessor.processTicketCreatedEvent(eventId, eventType, payload);
    }

    @RabbitListener(queues = "#{ticketAssignedQueue.name}")
    public void handleTicketAssignedEvent(
            TicketAssignedEvent payload,
            @Header("id") UUID eventId,
            @Header("eventType") String eventType
    ) {
        log.info("Received ticket assigned message: id={}, payload={}", eventId, payload);
        notificationProcessor.processTicketAssignedEvent(eventId, eventType, payload);
    }
}
