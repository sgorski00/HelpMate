package pl.sgorski.comment_service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@SpringBootApplication
@EnableScheduling
public class CommentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommentServiceApplication.class, args);
	}

	@Scheduled(cron = "0 * * * * *")
	public void scheduledTasks() {
		log.info("Comment service is still running...");
	}
}
