package pl.sgorski.comment_service.service;

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
import pl.sgorski.comment_service.config.properties.RabbitCommentExchangeProperties;
import pl.sgorski.common.event.CommentCreatedEvent;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @Mock
    private RabbitCommentExchangeProperties properties;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EventService eventService;

    @Mock
    private CommentCreatedEvent createdEvent;

    private String exchangeName;
    private String createdRoutingKey;

    @BeforeEach
    void setup() {
        exchangeName = "test.exchange";
        createdRoutingKey = "test.created";
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void cleanup() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    void shouldPublishCommentCreatedEvent() {
        when(properties.exchangeName()).thenReturn(exchangeName);
        when(properties.createdRoutingKey()).thenReturn(createdRoutingKey);

        eventService.publishCommentCreatedEvent(createdEvent);
        commitTransaction();

        verify(rabbitTemplate).convertAndSend(eq(exchangeName), eq(createdRoutingKey), eq(createdEvent), any(MessagePostProcessor.class));
    }

    @Test
    void shouldNotPublishCommentCreatedEvent_TransactionNotCommitted() {
        eventService.publishCommentCreatedEvent(createdEvent);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), Optional.of(anyString()), any(), any());
    }

    private void commitTransaction() {
        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }
    }
}
