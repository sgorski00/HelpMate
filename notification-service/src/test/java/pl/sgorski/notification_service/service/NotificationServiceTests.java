package pl.sgorski.notification_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationStatus;
import pl.sgorski.notification_service.repository.NotificationRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private Notification notification;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldSaveNotification_UpdateCreatedAt() {
        when(notificationRepository.save(any())).thenReturn(Mono.just(new Notification()));

        var result = notificationService.save(notification);

        assertTrue(result.blockOptional().isPresent());
        verify(notification, times(1)).setCreatedAt(any());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void shouldSaveNotification_DontUpdateCreatedAt() {
        when(notification.getCreatedAt()).thenReturn(Instant.MAX);
        when(notificationRepository.save(any())).thenReturn(Mono.just(new Notification()));

        var result = notificationService.save(notification);

        assertTrue(result.blockOptional().isPresent());
        verify(notification, never()).setCreatedAt(any());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateStatusById() {
        when(notificationRepository.findById(anyLong())).thenReturn(Mono.just(notification));
        when(notificationRepository.save(any())).thenReturn(Mono.just(new Notification()));

        var result = notificationService.updateStatusById(1L, NotificationStatus.SENT);

        assertTrue(result.blockOptional().isPresent());
        verify(notification, times(1)).setStatus(NotificationStatus.SENT);
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void shouldNotUpdateStatusById_NotFound() {
        when(notificationRepository.findById(anyLong())).thenReturn(Mono.empty());

        var result = notificationService.updateStatusById(1L, NotificationStatus.SENT);

        assertTrue(result.blockOptional().isEmpty());
        verify(notificationRepository, never()).save(any());
    }
}
