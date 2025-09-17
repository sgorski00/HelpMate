package pl.sgorski.notification_service.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.CommentCreatedEvent;
import pl.sgorski.notification_service.service.CommentNotificationProcessor;

import java.util.UUID;

@Log4j2
@Service
public class CommentEventListener {

    private final Queue commentCreatedQueue;
    private final CommentNotificationProcessor notificationProcessor;

    public CommentEventListener(
            @Qualifier("commentCreatedQueue") Queue commentCreatedQueue,
            CommentNotificationProcessor notificationProcessor
    ) {
        this.commentCreatedQueue = commentCreatedQueue;
        this.notificationProcessor = notificationProcessor;
    }

    @RabbitListener(queues = "#{commentCreatedQueue.name}")
    public void handleCommentCreatedEvent(
            CommentCreatedEvent payload,
            @Header("id") UUID eventId,
            @Header("eventType") String eventType
    ) {
        log.info("Received comment created message: id={}, payload={}", eventId, payload);
        notificationProcessor.processCommentCreatedEvent(eventId, eventType, payload).block(); // block to ensure event goes to dlq if fails
    }
}
