package pl.sgorski.ticket_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.sgorski.ticket_service.config.properties.KeycloakTicketClientProperties;
import pl.sgorski.ticket_service.config.properties.RabbitTicketExchangeProperties;
import pl.sgorski.ticket_service.config.properties.UserWebClientProperties;

@SpringBootApplication(scanBasePackages = {
		"pl.sgorski.ticket_service",
		"pl.sgorski.common",
		"pl.sgorski.security"
})
@EnableConfigurationProperties({UserWebClientProperties.class, KeycloakTicketClientProperties.class, RabbitTicketExchangeProperties.class})
public class TicketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketServiceApplication.class, args);
	}

}
