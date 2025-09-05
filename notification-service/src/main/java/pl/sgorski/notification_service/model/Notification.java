package pl.sgorski.notification_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import pl.sgorski.common.dto.UserDto;

import java.time.Instant;
import java.util.UUID;

@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {

    @Id
    private Long id;

    private UUID eventId;

    private String eventType;

    private UUID recipientId;

    private NotificationChannel channel;

    private String recipientAddress;

    private NotificationStatus status = NotificationStatus.PENDING;

    private Instant createdAt;

    private Instant sentAt;

    public Notification(UUID eventId, String eventType, NotificationChannel notificationChannel, UserDto user) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.channel = notificationChannel;
        this.recipientId = user.id();
        this.recipientAddress = user.email();
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
        if(status == NotificationStatus.SENT) {
            this.sentAt = Instant.now();
        }
    }
}
