package pl.sgorski.notification_service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.sgorski.notification_service.configuration.properties.RabbitTicketExchangeProperties;

@Log4j2
@SpringBootApplication(scanBasePackages = {
		"pl.sgorski.notification_service",
		"pl.sgorski.common"
})
@EnableScheduling
@EnableConfigurationProperties({RabbitTicketExchangeProperties.class})
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@Scheduled(cron = "0 * * * * *")
	public void alive() {
		NotificationServiceApplication.log.info("Notification Service is still running");
	}
}
