package pl.sgorski.notification_service.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketNotificationProcessorTests {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserClientService userClientService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private TicketNotificationProcessor processor;

    @Mock
    private UserDto user;

    @Mock
    private Notification notification;

    @Nested
    class ProcessTicketCreatedEventTests {

        @Mock
        private TicketCreatedEvent createdEvent;

        @Test
        void shouldProcessEventCorrectly() {
            when(createdEvent.reporterId()).thenReturn(UUID.randomUUID());
            when(userClientService.getUserById(any())).thenReturn(Mono.just(user));
            when(notificationService.save(any())).thenReturn(Mono.just(notification));
            when(user.email()).thenReturn("test@email.com");
            when(notificationService.updateStatusById(anyLong(), any())).thenReturn(Mono.just(new Notification()));

            StepVerifier.create(processor.processTicketCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                    .expectSubscription()
                    .verifyComplete();

            verify(mailService, times(1)).sendTicketCreatedEmail(eq(user.email()), eq(createdEvent));
            verifyNoMoreInteractions(mailService);
            verify(notificationService, times(1)).save(any());
            verify(notificationService, times(1)).updateStatusById(anyLong(), eq(NotificationStatus.SENT));
            verify(notificationService, never()).updateStatusById(anyLong(), eq(NotificationStatus.FAILED));
        }

        @Test
        void shouldNotProcessEventCorrectly_UserNotFound() {
            when(createdEvent.reporterId()).thenReturn(UUID.randomUUID());
            when(userClientService.getUserById(any())).thenReturn(Mono.empty());

            StepVerifier.create(processor.processTicketCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                    .expectSubscription()
                    .expectErrorMatches(ex -> ex instanceof UserNotFoundException)
                    .verify();

            verifyNoInteractions(mailService);
            verify(notificationService, never()).save(any());
            verify(notificationService, never()).updateStatusById(anyLong(), any());
        }

        @Test
        void shouldNotProcessEventCorrectly_MailSendingError() {
            when(createdEvent.reporterId()).thenReturn(UUID.randomUUID());
            when(userClientService.getUserById(any())).thenReturn(Mono.just(user));
            when(notificationService.save(any())).thenReturn(Mono.just(notification));
            when(user.email()).thenReturn("test@email.com");
            when(notificationService.updateStatusById(anyLong(), any())).thenReturn(Mono.just(new Notification()));
            doThrow(MailSendException.class).when(mailService).sendTicketCreatedEmail(anyString(), any());

            StepVerifier.create(processor.processTicketCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                    .expectSubscription()
                    .expectErrorSatisfies(ex -> {
                        assertInstanceOf(RuntimeException.class, ex);
                        assertTrue(ex.getMessage().contains("Failed to send notification"));
                    })
                    .verify();

            verify(mailService, times(1)).sendTicketCreatedEmail(eq(user.email()), eq(createdEvent));
            verifyNoMoreInteractions(mailService);
            verify(notificationService, times(1)).save(any());
            verify(notificationService, never()).updateStatusById(anyLong(), eq(NotificationStatus.SENT));
            verify(notificationService, times(1)).updateStatusById(anyLong(), eq(NotificationStatus.FAILED));
        }
    }

    @Nested
    class ProcessTicketAssignedEventTests {

        @Mock
        private TicketAssignedEvent assignedEvent;

        @Test
        void shouldProcessEventCorrectly() {
            when(assignedEvent.assigneeId()).thenReturn(UUID.randomUUID());
            when(userClientService.getUserById(any())).thenReturn(Mono.just(user));
            when(notificationService.save(any())).thenReturn(Mono.just(notification));
            when(user.email()).thenReturn("test@email.com");
            when(notificationService.updateStatusById(anyLong(), any())).thenReturn(Mono.just(new Notification()));

            StepVerifier.create(processor.processTicketAssignedEvent(UUID.randomUUID(), "event.test", assignedEvent))
                    .expectSubscription()
                    .verifyComplete();

            verify(mailService, times(1)).sendTicketAssignedEmail(eq(user.email()), eq(assignedEvent));
            verifyNoMoreInteractions(mailService);
            verify(notificationService, times(1)).save(any());
            verify(notificationService, times(1)).updateStatusById(anyLong(), eq(NotificationStatus.SENT));
            verify(notificationService, never()).updateStatusById(anyLong(), eq(NotificationStatus.FAILED));
        }

        @Test
        void shouldNotProcessEventCorrectly_UserNotFound() {
            when(assignedEvent.assigneeId()).thenReturn(UUID.randomUUID());
            when(userClientService.getUserById(any())).thenReturn(Mono.empty());

            StepVerifier.create(processor.processTicketAssignedEvent(UUID.randomUUID(), "event.test", assignedEvent))
                    .expectSubscription()
                    .expectErrorMatches(ex -> ex instanceof UserNotFoundException)
                    .verify();

            verifyNoInteractions(mailService);
            verify(notificationService, never()).save(any());
            verify(notificationService, never()).updateStatusById(anyLong(), any());
        }

        @Test
        void shouldNotProcessEventCorrectly_MailSendingError() {
            when(assignedEvent.assigneeId()).thenReturn(UUID.randomUUID());
            when(userClientService.getUserById(any())).thenReturn(Mono.just(user));
            when(notificationService.save(any())).thenReturn(Mono.just(notification));
            when(user.email()).thenReturn("test@email.com");
            when(notificationService.updateStatusById(anyLong(), any())).thenReturn(Mono.just(new Notification()));
            doThrow(MailSendException.class).when(mailService).sendTicketAssignedEmail(anyString(), any());

            StepVerifier.create(processor.processTicketAssignedEvent(UUID.randomUUID(), "event.test", assignedEvent))
                    .expectErrorSatisfies(ex -> {
                        assertInstanceOf(RuntimeException.class, ex);
                        assertTrue(ex.getMessage().contains("Failed to send notification"));
                    })
                    .verify();

            verify(mailService, times(1)).sendTicketAssignedEmail(eq(user.email()), eq(assignedEvent));
            verifyNoMoreInteractions(mailService);
            verify(notificationService, times(1)).save(any());
            verify(notificationService, never()).updateStatusById(anyLong(), eq(NotificationStatus.SENT));
            verify(notificationService, times(1)).updateStatusById(anyLong(), eq(NotificationStatus.FAILED));
        }
    }
}
