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

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private final NotificationService notificationService;
    private final UserClientService userClientService;
    private final MailService mailService;

    public void processTicketCreatedEvent(UUID eventId, String eventType, TicketCreatedEvent event) {
        UUID receiverId = event.reporterId();
        processTicketEvent(eventId, eventType, receiverId, email -> mailService.sendTicketCreatedEmail(email, event));
    }

    public void processTicketAssignedEvent(UUID eventId, String eventType, TicketAssignedEvent event) {
        UUID receiverId = event.assigneeId();
        processTicketEvent(eventId, eventType, receiverId, email -> mailService.sendTicketAssignedEmail(email, event));
    }

    private void processTicketEvent(UUID eventId, String eventType, UUID receiverId, Consumer<String> mailSender) {
        userClientService.getUserById(receiverId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + receiverId)))
                .flatMap(user -> {
                    Notification notification = new Notification(eventId, eventType, NotificationChannel.EMAIL, user);
                    return notificationService.save(notification)
                            .flatMap(saved -> Mono.just(user.email())
                                    .doOnNext(mailSender)
                                    .then(notificationService.updateStatusById(saved.getId(), NotificationStatus.SENT))
                            )
                            .onErrorResume(e -> notificationService.updateStatusById(notification.getId(), NotificationStatus.FAILED)
                                    .then(Mono.error(new RuntimeException("Failed to send notification: " + e)))
                            );
                })
                .doOnError(e -> log.error("Error processing ticket with id: {}, error message: {}", eventId, e.getMessage()))
                .subscribe();
    }
}
