package pl.sgorski.ticket_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.ticket_service.config.properties.RabbitTicketExchangeProperties;

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
                rabbitTemplate.convertAndSend(rabbitProperties.exchangeName(), rabbitProperties.createdRoutingKey(), event);
                log.info("Published TicketCreatedEvent for ticket with id: {}", event.ticketId());
            }
        });

    }
}
