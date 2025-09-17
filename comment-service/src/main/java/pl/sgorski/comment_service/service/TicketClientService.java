package pl.sgorski.comment_service.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class TicketClientService {

    private final WebClient ticketServiceWebClient;
    private final KeycloakTokenService keycloakTokenService;

    public TicketClientService(@Qualifier("ticketServiceWebClient") WebClient ticketServiceWebClient, KeycloakTokenService keycloakTokenService) {
        this.ticketServiceWebClient = ticketServiceWebClient;
        this.keycloakTokenService = keycloakTokenService;
    }

    public Mono<Boolean> isTicketCreator(Long ticketId, UUID userId) {
        return ticketServiceWebClient.get()
                .uri("/api/internal/tickets/{id}/is-creator/{userId}", ticketId, userId)
                .header("Authorization", "Bearer " + keycloakTokenService.getServiceToken())
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
