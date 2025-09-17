package pl.sgorski.notification_service.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.notification_service.configuration.properties.KeycloakNotificationClientProperties;
import reactor.core.publisher.Mono;

@Service
public class KeycloakTokenService {

    private final WebClient keycloakWebClient;
    private final KeycloakNotificationClientProperties keycloakNotificationClientProperties;

    public KeycloakTokenService(@Qualifier("keycloakWebClient") WebClient keycloakWebClient, KeycloakNotificationClientProperties keycloakNotificationClientProperties) {
        this.keycloakWebClient = keycloakWebClient;
        this.keycloakNotificationClientProperties = keycloakNotificationClientProperties;
    }

    public Mono<String> getServiceToken() {
        return keycloakWebClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", keycloakNotificationClientProperties.id())
                        .with("client_secret", keycloakNotificationClientProperties.secret()))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::accessToken);
    }

    private record TokenResponse (@JsonProperty("access_token") String accessToken) { }
}
