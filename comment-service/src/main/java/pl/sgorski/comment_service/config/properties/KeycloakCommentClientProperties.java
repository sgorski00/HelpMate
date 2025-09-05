package pl.sgorski.comment_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.comment-client")
public record KeycloakCommentClientProperties(
    String id,
    String secret,
    String tokenUri
) { }
