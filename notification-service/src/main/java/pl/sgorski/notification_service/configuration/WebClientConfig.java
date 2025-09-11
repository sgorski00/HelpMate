package pl.sgorski.notification_service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.notification_service.configuration.properties.KeycloakNotificationClientProperties;
import pl.sgorski.notification_service.configuration.properties.TicketWebClientProperties;
import pl.sgorski.notification_service.configuration.properties.UserWebClientProperties;

@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    private final UserWebClientProperties userWebClientProperties;
    private final TicketWebClientProperties ticketWebClientProperties;
    private final KeycloakNotificationClientProperties keycloakNotificationClientProperties;

    @Bean(name = "userServiceWebClient")
    public WebClient userServiceWebClient() {
        String url = userWebClientProperties.baseUrl();
        String port = userWebClientProperties.port();
        return WebClient.builder()
                .baseUrl(buildUrl(url, port))
                .build();
    }

    @Bean(name = "ticketServiceWebClient")
    public WebClient ticketServiceWebClient() {
        String url = ticketWebClientProperties.baseUrl();
        String port = ticketWebClientProperties.port();
        return WebClient.builder()
                .baseUrl(buildUrl(url, port))
                .build();
    }

    @Bean(name = "keycloakWebClient")
    public WebClient keycloakWebClient() {
        String tokenUri = keycloakNotificationClientProperties.tokenUri();
        return WebClient.builder()
                .baseUrl(tokenUri)
                .build();
    }

    private String buildUrl(String url, String port) {
        return port != null ? url + ":" + port : url;
    }
}
