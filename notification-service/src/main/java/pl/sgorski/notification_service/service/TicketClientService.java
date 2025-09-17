package pl.sgorski.notification_service.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.common.dto.TicketDto;
import reactor.core.publisher.Mono;

@Service
public class TicketClientService {

    private final WebClient ticketServiceWebClient;
    private final KeycloakTokenService keycloakTokenService;

    public TicketClientService(@Qualifier("ticketServiceWebClient") WebClient ticketServiceWebClient, KeycloakTokenService keycloakTokenService) {
        this.ticketServiceWebClient = ticketServiceWebClient;
        this.keycloakTokenService = keycloakTokenService;
    }

    public Mono<TicketDto> getTicketById(Long ticketId) {
        return keycloakTokenService.getServiceToken()
                .flatMap(token ->
                        ticketServiceWebClient.get()
                                .uri("/api/internal/tickets/{id}", ticketId)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToMono(TicketDto.class)
                );
    }
}
