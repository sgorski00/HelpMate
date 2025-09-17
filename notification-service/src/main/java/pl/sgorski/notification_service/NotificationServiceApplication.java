package pl.sgorski.notification_service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.sgorski.notification_service.configuration.properties.*;

@Log4j2
@SpringBootApplication(scanBasePackages = {
		"pl.sgorski.notification_service",
		"pl.sgorski.common"
})
@EnableConfigurationProperties({RabbitTicketExchangeProperties.class, MailProperties.class, KeycloakNotificationClientProperties.class, UserWebClientProperties.class, RabbitCommentExchangeProperties.class, TicketWebClientProperties.class})
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
