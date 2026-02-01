package com.sda.ticketing.service;

import com.sda.ticketing.domain.Ticket;
import com.sda.ticketing.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final boolean emailEnabled;
    private final String fromAddress;

    public NotificationService(JavaMailSender mailSender,
                               @Value("${notifications.email.enabled:false}") boolean emailEnabled,
                               @Value("${notifications.email.from:no-reply@example.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.emailEnabled = emailEnabled;
        this.fromAddress = fromAddress;
    }

    public void sendTicketCreated(Ticket ticket, User recipient) {
        if (!emailEnabled) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipient.getEmail());
        message.setSubject("[IT Tickets] Ticket created #" + ticket.getId());
        message.setText("Your ticket has been created: " + ticket.getTitle());
        mailSender.send(message);
    }
}

