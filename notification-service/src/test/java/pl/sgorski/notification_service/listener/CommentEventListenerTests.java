package pl.sgorski.notification_service.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import pl.sgorski.common.event.CommentCreatedEvent;
import pl.sgorski.notification_service.service.CommentNotificationProcessor;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentEventListenerTests {

    private final Queue commentCreatedQueue = new Queue(" commentCreatedQueueTest");

    @Mock
    private CommentNotificationProcessor processor;

    private CommentEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new CommentEventListener(
                commentCreatedQueue,
                processor
        );
    }

    @Test
    void shouldHandleCommentCreatedEvent() {
        CommentCreatedEvent event = mock(CommentCreatedEvent.class);
        when(processor.processCommentCreatedEvent(any(), anyString(), any())).thenReturn(Mono.when());

        listener.handleCommentCreatedEvent(event, UUID.randomUUID(), "event.test");

        verify(processor, times(1)).processCommentCreatedEvent(any(), anyString(), any());
    }
}
