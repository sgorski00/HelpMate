package pl.sgorski.notification_service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webclient.user")
public record UserWebClientProperties(
        String baseUrl,
        String port
) { }
