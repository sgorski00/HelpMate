package pl.sgorski.comment_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webclient.ticket")
public record TicketWebClientProperties(
        String baseUrl,
        String port
) { }
