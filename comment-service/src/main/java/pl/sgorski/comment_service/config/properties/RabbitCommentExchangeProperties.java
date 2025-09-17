package pl.sgorski.comment_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.comment")
public record RabbitCommentExchangeProperties(
        String exchangeName,
        String createdRoutingKey,
        String assignedRoutingKey
) { }
