package pl.sgorski.ticket_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import pl.sgorski.ticket_service.config.KeycloakTicketClientProperties;
import pl.sgorski.ticket_service.config.UserWebClientProperties;

@SpringBootApplication
@ComponentScan(basePackages = {
		"pl.sgorski.ticket_service",
		"pl.sgorski.security",
		"pl.sgorski.common"
})
@EnableConfigurationProperties({UserWebClientProperties.class, KeycloakTicketClientProperties.class})
public class TicketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketServiceApplication.class, args);
	}

}
