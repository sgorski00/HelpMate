package pl.sgorski.comment_service.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.comment_service.config.properties.KeycloakCommentClientProperties;

@Service
public class KeycloakTokenService {

    private final WebClient keycloakWebClient;
    private final KeycloakCommentClientProperties keycloakCommentClientProperties;

    public KeycloakTokenService(@Qualifier("keycloakWebClient") WebClient keycloakWebClient, KeycloakCommentClientProperties keycloakCommentClientProperties) {
        this.keycloakWebClient = keycloakWebClient;
        this.keycloakCommentClientProperties = keycloakCommentClientProperties;
    }

    public String getServiceToken() {
        return keycloakWebClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", keycloakCommentClientProperties.id())
                        .with("client_secret", keycloakCommentClientProperties.secret()))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::accessToken)
                .block();
    }

    private record TokenResponse (@JsonProperty("access_token") String accessToken) { }
}
