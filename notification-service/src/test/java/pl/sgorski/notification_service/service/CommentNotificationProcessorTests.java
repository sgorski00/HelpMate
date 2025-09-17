package pl.sgorski.notification_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import pl.sgorski.common.dto.TicketDto;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.common.event.CommentCreatedEvent;
import pl.sgorski.common.exception.TicketNotFoundException;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.notification_service.model.Notification;
import pl.sgorski.notification_service.model.NotificationStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentNotificationProcessorTests {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserClientService userClientService;

    @Mock
    private TicketClientService ticketClientService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private CommentNotificationProcessor processor;

    @Mock
    private CommentCreatedEvent createdEvent;

    @Mock
    private TicketDto ticket;

    @Mock
    private UserDto user;

    @Mock
    private Notification notification;

    @Test
    void shouldProcessCorrectly() {
        when(createdEvent.ticketId()).thenReturn(1L);
        when(createdEvent.authorId()).thenReturn(UUID.randomUUID());
        when(ticket.reporterId()).thenReturn(UUID.randomUUID());
        when(user.email()).thenReturn("test@email.com");
        when(ticketClientService.getTicketById(anyLong())).thenReturn(Mono.just(ticket));
        when(userClientService.getUserById(any())).thenReturn(Mono.just(user));
        when(notificationService.save(any())).thenReturn(Mono.just(notification));
        when(notificationService.updateStatusById(anyLong(), any())).thenReturn(Mono.just(new Notification()));

        StepVerifier.create(processor.processCommentCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                .expectSubscription()
                .verifyComplete();

        verify(notificationService, times(1)).save(any());
        verify(notificationService, times(1)).updateStatusById(anyLong(), eq(NotificationStatus.SENT));
        verify(mailService, times(1)).sendCommentCreatedEmail(eq(user.email()), any());
    }

    @Test
    void shouldNotProcess_TicketNotFound() {
        when(createdEvent.ticketId()).thenReturn(1L);
        when(ticketClientService.getTicketById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(processor.processCommentCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                .expectSubscription()
                .expectErrorMatches(ex -> ex instanceof TicketNotFoundException)
                .verify();

        verify(notificationService, never()).save(any());
        verifyNoInteractions(mailService);
    }

    @Test
    void shouldNotSendNotification_CommentAuthorIsReporter() {
        UUID authorId = UUID.randomUUID();
        when(ticket.reporterId()).thenReturn(authorId);
        when(createdEvent.authorId()).thenReturn(authorId);
        when(createdEvent.ticketId()).thenReturn(1L);
        when(ticketClientService.getTicketById(anyLong())).thenReturn(Mono.just(ticket));

        StepVerifier.create(processor.processCommentCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                .expectSubscription()
                .verifyComplete();

        verify(notificationService, never()).save(any());
        verify(notificationService, never()).updateStatusById(anyLong(), any());
        verifyNoInteractions(mailService);
    }

    @Test
    void shouldNotProcess_UserNotFound() {
        when(createdEvent.ticketId()).thenReturn(1L);
        when(createdEvent.authorId()).thenReturn(UUID.randomUUID());
        when(ticket.reporterId()).thenReturn(UUID.randomUUID());
        when(ticketClientService.getTicketById(anyLong())).thenReturn(Mono.just(ticket));
        when(userClientService.getUserById(any())).thenReturn(Mono.empty());

        StepVerifier.create(processor.processCommentCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                .expectSubscription()
                .expectErrorMatches(ex -> ex instanceof UserNotFoundException)
                .verify();

        verify(notificationService, never()).save(any());
        verify(notificationService, never()).updateStatusById(anyLong(), any());
        verifyNoInteractions(mailService);
    }

    @Test
    void shouldNotProcessCorrectly_MailSendingError() {
        when(createdEvent.ticketId()).thenReturn(1L);
        when(createdEvent.authorId()).thenReturn(UUID.randomUUID());
        when(ticket.reporterId()).thenReturn(UUID.randomUUID());
        when(user.email()).thenReturn("test@email.com");
        when(ticketClientService.getTicketById(anyLong())).thenReturn(Mono.just(ticket));
        when(userClientService.getUserById(any())).thenReturn(Mono.just(user));
        when(notificationService.save(any())).thenReturn(Mono.just(notification));
        when(notificationService.updateStatusById(anyLong(), any())).thenReturn(Mono.just(new Notification()));
        doThrow(new MailSendException("Error while sending email")).when(mailService).sendCommentCreatedEmail(anyString(), any());

        StepVerifier.create(processor.processCommentCreatedEvent(UUID.randomUUID(), "event.test", createdEvent))
                .expectSubscription()
                .expectErrorSatisfies(ex -> {
                    assertInstanceOf(RuntimeException.class, ex);
                    assertTrue(ex.getMessage().contains("Failed to send notification"));
                })
                .verify();

        verify(notificationService, times(1)).save(any());
        verify(notificationService, never()).updateStatusById(anyLong(), eq(NotificationStatus.SENT));
        verify(notificationService, times(1)).updateStatusById(anyLong(), eq(NotificationStatus.FAILED));
        verify(mailService, times(1)).sendCommentCreatedEmail(eq(user.email()), any());
    }
}
