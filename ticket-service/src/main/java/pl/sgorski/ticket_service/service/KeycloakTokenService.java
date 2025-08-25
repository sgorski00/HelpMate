package pl.sgorski.ticket_service.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.ticket_service.config.properties.KeycloakTicketClientProperties;

@Service
public class KeycloakTokenService {

    private final WebClient keycloakWebClient;
    private final KeycloakTicketClientProperties keycloakUserClientProperties;

    public KeycloakTokenService(@Qualifier("keycloakWebClient") WebClient keycloakWebClient, KeycloakTicketClientProperties keycloakUserClientProperties) {
        this.keycloakWebClient = keycloakWebClient;
        this.keycloakUserClientProperties = keycloakUserClientProperties;
    }

    public String getServiceToken() {
        return keycloakWebClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", keycloakUserClientProperties.id())
                        .with("client_secret", keycloakUserClientProperties.secret()))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::accessToken)
                .block();
    }

    record TokenResponse (@JsonProperty("access_token") String accessToken) { }
}
