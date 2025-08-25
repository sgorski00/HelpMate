package pl.sgorski.ticket_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.ticket_service.config.properties.KeycloakTicketClientProperties;
import pl.sgorski.ticket_service.config.properties.UserWebClientProperties;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final UserWebClientProperties userWebClientProperties;
    private final KeycloakTicketClientProperties keycloakTicketClientProperties;

    @Bean(name = "userServiceWebClient")
    public WebClient userServiceWebClient() {
        String url = userWebClientProperties.baseUrl();
        String port = userWebClientProperties.port();
        return WebClient.builder()
                .baseUrl(buildUrl(url, port))
                .build();
    }

    @Bean(name = "keycloakWebClient")
    public WebClient keycloakWebClient() {
        String tokenUri = keycloakTicketClientProperties.tokenUri();
        return WebClient.builder()
                .baseUrl(tokenUri)
                .build();
    }

    private String buildUrl(String url, String port) {
        return port != null ? url + ":" + port : url;
    }
}
