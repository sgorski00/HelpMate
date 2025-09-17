package pl.sgorski.notification_service.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.notification_service.service.TicketNotificationProcessor;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketEventListenerTests {

    private final Queue ticketCreatedQueue = new Queue("ticketCreatedQueueTest");
    private final Queue ticketAssignedQueue = new Queue("ticketAssignedQueueTest");

    @Mock
    private TicketNotificationProcessor processor;

    private TicketEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new TicketEventListener(
                ticketCreatedQueue,
                ticketAssignedQueue,
                processor
        );
    }

    @Test
    void shouldHandleTicketCreatedEvent() {
        TicketCreatedEvent event = mock(TicketCreatedEvent.class);
        when(processor.processTicketCreatedEvent(any(), anyString(), any())).thenReturn(Mono.when());

        listener.handleTicketCreatedEvent(event, UUID.randomUUID(), "event.test");

        verify(processor, times(1)).processTicketCreatedEvent(any(), anyString(), any());
    }

    @Test
    void shouldHandleTicketAssignedEvent() {
        TicketAssignedEvent event = mock(TicketAssignedEvent.class);
        when(processor.processTicketAssignedEvent(any(), anyString(), any())).thenReturn(Mono.when());

        listener.handleTicketAssignedEvent(event, UUID.randomUUID(), "event.test");

        verify(processor, times(1)).processTicketAssignedEvent(any(), anyString(), any());
    }
}
