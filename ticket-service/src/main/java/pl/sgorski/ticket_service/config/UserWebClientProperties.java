package pl.sgorski.ticket_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webclient.user")
public record UserWebClientProperties(
        String baseUrl,
        String port
)
{}
