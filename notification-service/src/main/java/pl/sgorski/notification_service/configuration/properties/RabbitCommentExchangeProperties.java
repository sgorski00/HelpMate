package pl.sgorski.notification_service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.comment")
public record RabbitCommentExchangeProperties(
        String exchangeName,
        String createdRoutingKey,
        String createdQueue,
        String dlx,
        String dlq
) { }
