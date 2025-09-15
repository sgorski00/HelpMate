package pl.sgorski.ticket_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.ticket_service.config.properties.RabbitTicketExchangeProperties;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTests {

    @Mock
    private RabbitTicketExchangeProperties rabbitProperties;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setup() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void cleanup() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    void shouldPublishTicketCreatedEventAfterTransactionCommit() {
        when(rabbitProperties.exchangeName()).thenReturn("test.exchange");
        when(rabbitProperties.createdRoutingKey()).thenReturn("test.created");
        TicketCreatedEvent event = mock(TicketCreatedEvent.class);
        when(event.ticketId()).thenReturn("123");

        eventService.publishTicketCreatedEvent(event);
        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        verify(rabbitTemplate).convertAndSend(eq("test.exchange"), eq("test.created"), eq(event), any(MessagePostProcessor.class));
    }

    @Test
    void shouldPublishTicketAssignedEventAfterTransactionCommit() {
        when(rabbitProperties.exchangeName()).thenReturn("test.exchange");
        when(rabbitProperties.assignedRoutingKey()).thenReturn("test.assigned");
        TicketAssignedEvent event = mock(TicketAssignedEvent.class);
        when(event.ticketId()).thenReturn("456");

        eventService.publishTicketAssignedEvent(event);
        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        verify(rabbitTemplate).convertAndSend(eq("test.exchange"), eq("test.assigned"), eq(event), any(MessagePostProcessor.class));
    }

    @Test
    void shouldNotPublishEventIfTransactionNotCommitted() {
        TicketCreatedEvent event = mock(TicketCreatedEvent.class);

        eventService.publishTicketCreatedEvent(event);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), Optional.of(anyString()), any(), any());
    }
}