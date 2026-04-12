package com.pruebalib.notification.provider.smtp;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

public final class SmtpClient {

    public String send(SmtpPayload payload, SmtpAuthenticator authenticator, SmtpConfig config) {
        if (payload == null) {
            throw new IllegalArgumentException("SmtpPayload no puede ser nulo");
        }
        if (authenticator == null) {
            throw new IllegalArgumentException("SmtpAuthenticator no puede ser nulo");
        }
        if (config == null) {
            throw new IllegalArgumentException("SmtpConfig no puede ser nulo");
        }

        authenticator.validate();

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", config.host());
        properties.put("mail.smtp.port", String.valueOf(config.port()));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", String.valueOf(config.startTls()));
        properties.put("mail.smtp.ssl.enable", String.valueOf(config.ssl()));
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");

        Session session = Session.getInstance(properties, authenticator.toJavaMailAuthenticator());

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.from()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(payload.getRecipient()));
            message.setSubject(payload.getSubject());
            message.setText(payload.getBody(), StandardCharsets.UTF_8.name());

            Transport.send(message);

            String[] messageIdHeader = message.getHeader("Message-ID");
            return messageIdHeader != null && messageIdHeader.length > 0
                    ? messageIdHeader[0]
                    : "smtp-" + UUID.randomUUID();
        } catch (MessagingException e) {
            throw new RuntimeException("No se pudo enviar el correo SMTP", e);
        }
    }
}
