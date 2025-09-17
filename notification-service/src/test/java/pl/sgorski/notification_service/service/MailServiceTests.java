package pl.sgorski.notification_service.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import pl.sgorski.common.event.CommentCreatedEvent;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.notification_service.configuration.properties.MailProperties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTests {

    @Mock
    private  JavaMailSender mailSender;

    @Mock
    private MailProperties mailProperties;

    @InjectMocks
    private MailService mailService;

    @Mock
    private MimeMessage mimeMessage;

    @Nested
    class SendTicketCreatedEmail {

        @Mock
        private TicketCreatedEvent event;

        @Test
        void shouldSendEmailSuccessfully() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(mailProperties.defaultEncoding()).thenReturn("UTF-8");
            when(mailProperties.username()).thenReturn("test@example.com");

            mailService.sendTicketCreatedEmail("recipient@example.com", event);

            verify(mailSender).send(mimeMessage);
        }

        @Test
        void shouldThrowMailSendException() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(mailProperties.defaultEncoding()).thenReturn("UTF-8");
            when(mailProperties.username()).thenReturn("test@example.com");
            doThrow(new RuntimeException("Some mailing exception")).when(mailSender).send(any(MimeMessage.class));

            assertThrows(MailSendException.class, () -> mailService.sendTicketCreatedEmail("recipient@example.com", event));
        }
    }

    @Nested
    class SendTicketAssignedEmail {

        @Mock
        private TicketAssignedEvent event;

        @Test
        void shouldSendEmailSuccessfully() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(mailProperties.defaultEncoding()).thenReturn("UTF-8");
            when(mailProperties.username()).thenReturn("test@example.com");

            mailService.sendTicketAssignedEmail("recipient@example.com", event);

            verify(mailSender).send(mimeMessage);
        }

        @Test
        void shouldThrowMailSendException() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(mailProperties.defaultEncoding()).thenReturn("UTF-8");
            when(mailProperties.username()).thenReturn("test@example.com");
            doThrow(new RuntimeException("Some mailing exception")).when(mailSender).send(any(MimeMessage.class));

            assertThrows(MailSendException.class, () -> mailService.sendTicketAssignedEmail("recipient@example.com", event));
        }
    }

    @Nested
    class SendCommentCreatedEmail {

        @Mock
        private CommentCreatedEvent event;

        @Test
        void shouldSendEmailWithCommentContent() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(mailProperties.defaultEncoding()).thenReturn("UTF-8");
            when(mailProperties.username()).thenReturn("test@example.com");

            mailService.sendCommentCreatedEmail("recipient@example.com", event);

            verify(mailSender).send(mimeMessage);
        }

        @Test
        void shouldThrowMailSendException() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(mailProperties.defaultEncoding()).thenReturn("UTF-8");
            when(mailProperties.username()).thenReturn("test@example.com");

            doThrow(new RuntimeException("Some mailing exception")).when(mailSender).send(mimeMessage);

            assertThrows(MailSendException.class, () -> mailService.sendCommentCreatedEmail("recipient@example.com", event));
        }
    }
}
