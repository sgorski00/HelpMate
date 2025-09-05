package pl.sgorski.comment_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sgorski.comment_service.config.properties.KeycloakCommentClientProperties;
import pl.sgorski.comment_service.config.properties.UserWebClientProperties;

import static pl.sgorski.common.utils.UrlUtils.buildUrl;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final UserWebClientProperties userWebClientProperties;
    private final KeycloakCommentClientProperties keycloakCommentClientProperties;

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
        String tokenUri = keycloakCommentClientProperties.tokenUri();
        return WebClient.builder()
                .baseUrl(tokenUri)
                .build();
    }

}
