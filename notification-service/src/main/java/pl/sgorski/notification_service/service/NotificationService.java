package pl.sgorski.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.TicketEvent;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationStatus;
import pl.sgorski.notification_service.repository.NotificationRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Mono<Notification> save(Notification notification) {
        if(notification.getCreatedAt() == null) notification.setCreatedAt(Instant.now());
        return notificationRepository.save(notification);
    }

    public Mono<Notification> updateStatusById(Long id, NotificationStatus status) {
        return notificationRepository.findById(id)
                .flatMap(n -> {
                    n.setStatus(status);
                    return notificationRepository.save(n);
                });
    }
}
