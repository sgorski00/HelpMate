package pl.sgorski.notification_service.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationChannel;
import pl.sgorski.notification_service.model.NotificationStatus;
import pl.sgorski.notification_service.service.MailService;
import pl.sgorski.notification_service.service.NotificationService;
import pl.sgorski.notification_service.service.UserClientService;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Log4j2
public class TicketEventListener {

    private final Queue ticketCreatedQueue;
    private final Queue ticketAssignedQueue;
    private final MailService mailService;
    private final NotificationService notificationService;
    private final UserClientService userClientService;

    public TicketEventListener(
            @Qualifier("ticketCreatedQueue") Queue ticketCreatedQueue,
            @Qualifier("ticketAssignedQueue") Queue ticketAssignedQueue,
            MailService mailService,
            NotificationService notificationService,
            UserClientService userClientService) {
        this.ticketCreatedQueue = ticketCreatedQueue;
        this.ticketAssignedQueue = ticketAssignedQueue;
        this.mailService = mailService;
        this.notificationService = notificationService;
        this.userClientService = userClientService;
    }

    @RabbitListener(queues = "#{ticketCreatedQueue.name}")
    public void handleTicketCreatedEvent(
            TicketCreatedEvent payload,
            @Header("id") UUID eventId,
            @Header("eventType") String eventType
    ) {
        log.info("Received ticket created message: id={}, payload={}", eventId, payload);
        userClientService.getUserById(payload.reporterId())
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + payload.reporterId())))
                .flatMap(user -> {
                    Notification notification = new Notification(eventId, eventType, NotificationChannel.EMAIL, user);
                    return notificationService.save(notification)
                            .flatMap(saved ->
                                    Mono.fromRunnable(() -> mailService.sendTicketCreatedEmail(user.email(), payload))
                                            .then(notificationService.updateStatusById(saved.getId(), NotificationStatus.SENT))
                            )
                            .onErrorResume(e -> notificationService.updateStatusById(notification.getId(), NotificationStatus.FAILED)
                                    .then(Mono.error(new RuntimeException("Failed to send notification: " + e)))
                            );
                })
                .doOnError(e -> log.error("Error processing ticket with id: {}, error message: {}", eventId, e.getMessage()))
                .block();
    }

    @RabbitListener(queues = "#{ticketAssignedQueue.name}")
    public void handleTicketAssignedEvent(
            TicketAssignedEvent payload,
            @Header("id") UUID eventId,
            @Header("eventType") String eventType
    ) {
        log.info("Received ticket assigned message: id={}, payload={}", eventId, payload);
        userClientService.getUserById(payload.assigneeId())
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + payload.assigneeId())))
                .flatMap(user -> {
                    Notification notification = new Notification(eventId, eventType, NotificationChannel.EMAIL, user);
                    return notificationService.save(notification)
                            .flatMap(saved ->
                                    Mono.fromRunnable(() -> mailService.sendTicketAssignedEmail(user.email(), payload))
                                            .then(notificationService.updateStatusById(saved.getId(), NotificationStatus.SENT))
                            )
                            .onErrorResume(e -> notificationService.updateStatusById(notification.getId(), NotificationStatus.FAILED)
                                    .then(Mono.error(new RuntimeException("Failed to send notification: " + e)))
                            );
                })
                .doOnError(e -> log.error("Error processing ticket with id: {}, exception message: {}", eventId, e.getMessage()))
                .block();
    }
}
