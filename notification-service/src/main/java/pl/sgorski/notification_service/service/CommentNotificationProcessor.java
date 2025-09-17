package pl.sgorski.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.CommentCreatedEvent;
import pl.sgorski.common.exception.TicketNotFoundException;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationChannel;
import pl.sgorski.notification_service.model.NotificationStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class CommentNotificationProcessor {

    private final NotificationService notificationService;
    private final UserClientService userClientService;
    private final TicketClientService ticketClientService;
    private final MailService mailService;

    public Mono<Void> processCommentCreatedEvent(UUID eventId, String eventType, CommentCreatedEvent event) {
        Long ticketId = event.ticketId();
        return ticketClientService.getTicketById(ticketId)
                .switchIfEmpty(Mono.error(new TicketNotFoundException("Ticket not found with id: " + ticketId)))
                .flatMap(ticket -> {
                    if (ticket.reporterId().equals(event.authorId())) {
                        log.info("Skipping notification for comment with id: {} as author is the same as reporter", event.id());
                        return Mono.empty();
                    }
                    return userClientService.getUserById(ticket.reporterId())
                            .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + ticketId)))
                            .flatMap(recipient -> {
                                Notification notification = new Notification(eventId, eventType, NotificationChannel.EMAIL, recipient);
                                return notificationService.save(notification)
                                        .flatMap(saved -> Mono.fromCallable(() -> {
                                                            mailService.sendCommentCreatedEmail(recipient.email(), event);
                                                            return saved.getId();
                                                        })
                                                        .flatMap(id -> notificationService.updateStatusById(id, NotificationStatus.SENT))
                                                        .onErrorResume(e -> notificationService.updateStatusById(saved.getId(), NotificationStatus.FAILED)
                                                                .then(Mono.error(new RuntimeException("Failed to send notification: " + e)))
                                                        )
                                        );
                            });
                })
                .doOnError(e -> log.error("Error processing notification for comment with id: {}, error message: {}", event.id(), e.getMessage()))
                .then();
    }
}
