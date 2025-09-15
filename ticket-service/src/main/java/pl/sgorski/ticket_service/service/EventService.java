package pl.sgorski.ticket_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.common.event.TicketEvent;
import pl.sgorski.ticket_service.config.properties.RabbitTicketExchangeProperties;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventService {

    private final RabbitTicketExchangeProperties rabbitProperties;
    private final RabbitTemplate rabbitTemplate;

    public void publishTicketCreatedEvent(TicketCreatedEvent event) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendEvent(event, rabbitProperties.createdRoutingKey(), "ticket.created");
            }
        });
    }

    public void publishTicketAssignedEvent(TicketAssignedEvent event) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendEvent(event, rabbitProperties.assignedRoutingKey(), "ticket.assigned");
            }
        });
    }

    private void sendEvent(TicketEvent event, String routingKey, String eventType) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.exchangeName(),
                routingKey,
                event,
                message -> initMessage(eventType, message)
        );
        log.info("Published {} event for ticket with id: {}", eventType, event.ticketId());
    }

    private Message initMessage(String eventType, Message message) {
        var props = message.getMessageProperties();
        props.setMessageId(UUID.randomUUID().toString());
        props.setHeader("eventType", eventType);
        return message;
    }
}
