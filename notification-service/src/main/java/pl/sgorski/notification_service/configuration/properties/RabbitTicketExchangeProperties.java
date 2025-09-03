package pl.sgorski.notification_service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.ticket")
public record RabbitTicketExchangeProperties(
        String exchangeName,
        String createdRoutingKey,
        String createdQueue,
        String assignedRoutingKey,
        String assignedQueue,
        String dlx,
        String dlq
) { }
