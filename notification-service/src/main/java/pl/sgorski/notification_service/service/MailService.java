package pl.sgorski.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.sgorski.common.event.CommentCreatedEvent;
import pl.sgorski.common.event.TicketAssignedEvent;
import pl.sgorski.common.event.TicketCreatedEvent;
import pl.sgorski.notification_service.configuration.properties.MailProperties;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    private void sendEmail(String to, String subject, String body) {
        boolean isMultipart = true;
        boolean isHtml = true;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, isMultipart, mailProperties.defaultEncoding());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);
            helper.setFrom(mailProperties.username());
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new MailSendException("Failed to send email to " + to, e);
        }
    }

    public void sendTicketCreatedEmail(String to, TicketCreatedEvent payload) throws MailSendException {
        String subject = "HelpMate - Ticket no. " + payload.ticketId() + " Created";
        String body = "<p>Your ticket has been created successfully.</p>";
        sendEmail(to, subject, body);
    }

    public void sendTicketAssignedEmail(String to, TicketAssignedEvent payload) {
        String subject = "HelpMate - Ticket no. " + payload.ticketId() + " has been assigned to you";
        String body = "<p>Check the application. A new ticket has been assigned to you.</p>";
        sendEmail(to, subject, body);
    }

    public void sendCommentCreatedEmail(String to, CommentCreatedEvent payload) {
        String subject = "HelpMate - New comment on your ticket no. " + payload.ticketId();
        String body = "<p>A new comment has been added to your ticket. Check the details below.</p><br>" +
                "<p><b>Message:</b>" + payload.content() + "</p>";
        sendEmail(to, subject, body);
    }
}
