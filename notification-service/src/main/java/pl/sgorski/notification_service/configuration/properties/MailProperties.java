package pl.sgorski.notification_service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(
        String username,
        String defaultEncoding
) { }
