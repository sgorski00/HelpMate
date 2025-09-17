package pl.sgorski.notification_service.model;

import org.junit.jupiter.api.Test;
import pl.sgorski.common.dto.UserDto;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationTests {

    @Test
    void shouldCreateNotification() {
        UUID eventId = UUID.randomUUID();
        String eventType = "event.created";
        NotificationChannel channel = NotificationChannel.EMAIL;
        UserDto user = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", "Test", "User", Set.of());

        var notification = new Notification(eventId, eventType, channel, user);

        assertEquals(eventId, notification.getEventId());
        assertEquals(eventType, notification.getEventType());
        assertEquals(channel, notification.getChannel());
        assertEquals(user.id(), notification.getRecipientId());
        assertEquals(user.email(), notification.getRecipientAddress());
    }

    @Test
    void shouldSetStatus_Sent() {
        var notification = new Notification();

        notification.setStatus(NotificationStatus.SENT);

        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertNotNull(notification.getSentAt());
    }

    @Test
    void shouldSetStatus_Others() {
        var notification = new Notification();

        notification.setStatus(NotificationStatus.PENDING);

        assertEquals(NotificationStatus.PENDING, notification.getStatus());
        assertNull(notification.getSentAt());
    }
}
