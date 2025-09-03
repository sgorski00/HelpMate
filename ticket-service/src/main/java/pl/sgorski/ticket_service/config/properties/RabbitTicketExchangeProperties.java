package pl.sgorski.ticket_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.ticket")
public record RabbitTicketExchangeProperties(
        String exchangeName,
        String createdRoutingKey,
        String assignedRoutingKey
) {
}
