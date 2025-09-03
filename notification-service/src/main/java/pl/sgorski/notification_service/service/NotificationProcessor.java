package pl.sgorski.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationChannel;
import pl.sgorski.notification_service.model.NotificationStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private final NotificationService notificationService;
    private final UserClientService userClientService;

    public void process(UUID eventId, String eventType, String receiverId, Runnable mailSender) {
        userClientService.getUserById(receiverId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + receiverId)))
                .flatMap(user -> {
                    Notification notification = new Notification(eventId, eventType, NotificationChannel.EMAIL, user);
                    return notificationService.save(notification)
                            .flatMap(saved ->
                                    Mono.fromRunnable(mailSender)
                                            .then(notificationService.updateStatusById(saved.getId(), NotificationStatus.SENT))
                            )
                            .onErrorResume(e -> notificationService.updateStatusById(notification.getId(), NotificationStatus.FAILED)
                                    .then(Mono.error(new RuntimeException("Failed to send notification: " + e)))
                            );
                })
                .doOnError(e -> log.error("Error processing ticket with id: {}, error message: {}", eventId, e.getMessage()))
                .block();
    }
}
