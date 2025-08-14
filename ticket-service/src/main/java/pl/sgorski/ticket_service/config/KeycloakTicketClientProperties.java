package pl.sgorski.ticket_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.ticket-client")
public record KeycloakTicketClientProperties(
    String id,
    String secret,
    String tokenUri
)
{}
