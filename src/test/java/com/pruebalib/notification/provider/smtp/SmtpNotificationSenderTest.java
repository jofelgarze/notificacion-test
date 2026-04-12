package com.pruebalib.notification.provider.smtp;

import com.pruebalib.notification.api.NotificationMetadata;
import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.core.NotificationAttributes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SmtpNotificationSenderTest {

    @Test
    void shouldSupportSmtpEmailRequests() {
        NotificationMetadata metadata = new NotificationMetadata(Map.of(
                NotificationAttributes.CHANNEL, "email",
                NotificationAttributes.PROVIDER, "smtp",
                "smtp.username", "user@example.com",
                "smtp.password", "secret",
                "smtp.host", "smtp.example.com",
                "smtp.port", "587"));

        NotificationRequest request = new NotificationRequest(
                "dest@example.com",
                "Prueba SMTP",
                "Mensaje de prueba",
                metadata);

        SmtpNotificationSender sender = new SmtpNotificationSender();
        assertTrue(sender.supports(request));
    }

    @Test
    void shouldNotSupportNonSmtpRequests() {
        NotificationMetadata metadata = new NotificationMetadata(Map.of(
                NotificationAttributes.CHANNEL, "email",
                NotificationAttributes.PROVIDER, "gmail",
                "gmail.username", "user@gmail.com",
                "gmail.password", "secret"));

        NotificationRequest request = new NotificationRequest(
                "dest@example.com",
                "Prueba Gmail",
                "Mensaje de prueba",
                metadata);

        SmtpNotificationSender sender = new SmtpNotificationSender();
        assertFalse(sender.supports(request));
    }
}
