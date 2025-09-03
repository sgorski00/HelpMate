package pl.sgorski.notification_service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.notification-client")
public record KeycloakNotificationClientProperties(
        String tokenUri,
        String id,
        String secret
) { }
