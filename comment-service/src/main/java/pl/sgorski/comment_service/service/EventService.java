package pl.sgorski.comment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pl.sgorski.comment_service.config.properties.RabbitCommentExchangeProperties;
import pl.sgorski.common.event.CommentEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class EventService {

    private final RabbitCommentExchangeProperties rabbitProperties;
    private final RabbitTemplate rabbitTemplate;

    public void publishCommentCreatedEvent(CommentEvent event) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rabbitTemplate.convertAndSend(
                        rabbitProperties.exchangeName(),
                        rabbitProperties.createdRoutingKey(),
                        event,
                        message -> {
                            var props = message.getMessageProperties();
                            props.setMessageId(UUID.randomUUID().toString());
                            props.setHeader("eventType", "comment.created");
                            return message;
                        }
                );
                log.info("Published comment.created event for comment with id: {}", event.id());
            }
        });
    }
}
