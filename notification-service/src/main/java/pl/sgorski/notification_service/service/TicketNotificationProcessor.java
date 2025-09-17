package pl.sgorski.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationChannel;
import pl.sgorski.notification_service.model.NotificationStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

@Log4j2
@Service
@RequiredArgsConstructor
public class TicketNotificationProcessor {

    private final NotificationService notificationService;
    private final UserClientService userClientService;
    private final MailService mailService;

    public Mono<Void> processTicketCreatedEvent(UUID eventId, String eventType, TicketCreatedEvent event) {
        UUID receiverId = event.reporterId();
        return processTicketEvent(eventId, eventType, receiverId, email -> mailService.sendTicketCreatedEmail(email, event));
    }

    public Mono<Void> processTicketAssignedEvent(UUID eventId, String eventType, TicketAssignedEvent event) {
        UUID receiverId = event.assigneeId();
        return processTicketEvent(eventId, eventType, receiverId, email -> mailService.sendTicketAssignedEmail(email, event));
    }

    private Mono<Void> processTicketEvent(UUID eventId, String eventType, UUID receiverId, Consumer<String> mailSender) {
        return userClientService.getUserById(receiverId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + receiverId)))
                .flatMap(user -> {
                    Notification notification = new Notification(eventId, eventType, NotificationChannel.EMAIL, user);
                    return notificationService.save(notification)
                            .flatMap(saved -> Mono.fromCallable(() -> {
                                                mailSender.accept(user.email());
                                                return saved.getId();
                                            })
                                            .flatMap(notificationId -> notificationService.updateStatusById(notificationId, NotificationStatus.SENT))
                                            .onErrorResume(e -> notificationService.updateStatusById(saved.getId(), NotificationStatus.FAILED)
                                                    .then(Mono.error(new RuntimeException("Failed to send notification: " + e)))
                                            )
                            );
                })
                .doOnError(e -> log.error("Error processing ticket notification with id: {}, error message: {}", eventId, e.getMessage()))
                .then();
    }
}
