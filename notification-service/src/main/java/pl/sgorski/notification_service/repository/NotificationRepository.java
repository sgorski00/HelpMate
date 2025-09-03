package pl.sgorski.notification_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.sgorski.notification_service.model.Notification;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
}
