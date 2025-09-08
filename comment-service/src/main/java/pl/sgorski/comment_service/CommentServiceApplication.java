package pl.sgorski.comment_service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.sgorski.comment_service.config.properties.KeycloakCommentClientProperties;
import pl.sgorski.comment_service.config.properties.TicketWebClientProperties;
import pl.sgorski.comment_service.config.properties.UserWebClientProperties;

@Log4j2
@SpringBootApplication(scanBasePackages = {
		"pl.sgorski.comment_service",
		"pl.sgorski.common",
		"pl.sgorski.security"}
)
@EnableConfigurationProperties({KeycloakCommentClientProperties.class, UserWebClientProperties.class, TicketWebClientProperties.class})
public class CommentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommentServiceApplication.class, args);
	}
}
