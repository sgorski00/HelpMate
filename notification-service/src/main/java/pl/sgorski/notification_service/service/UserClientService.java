package pl.sgorski.notification_service.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.common.dto.UserDto;
import reactor.core.publisher.Mono;

@Service
public class UserClientService {

    private final WebClient userServiceWebClient;
    private final KeycloakTokenService keycloakTokenService;

    public UserClientService(@Qualifier("userServiceWebClient") WebClient userServiceWebClient, KeycloakTokenService keycloakTokenService) {
        this.userServiceWebClient = userServiceWebClient;
        this.keycloakTokenService = keycloakTokenService;
    }

    public Mono<UserDto> getUserById(String userId) {
        return userServiceWebClient.get()
                .uri("/api/internal/users/{id}", userId)
                .header("Authorization", "Bearer " + keycloakTokenService.getServiceToken())
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}
