package com.pruebalib.notification.provider.smtp;

import com.pruebalib.notification.api.NotificationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmtpNotificationSenderTest {

    @Test
    void shouldSupportSmtpRequests() {
        NotificationRequest request = new NotificationRequest(
                "smtp",
                "dest@example.com",
                "Prueba SMTP",
                "Mensaje de prueba");

        SmtpNotificationSender sender = new SmtpNotificationSender(
                new SmtpConfig("user@example.com", "secret", null, "smtp.example.com", 587, true, false));

        assertTrue(sender.supports(request));
    }

    @Test
    void shouldNotSupportNonSmtpRequests() {
        NotificationRequest request = new NotificationRequest(
                "gmail",
                "dest@example.com",
                "Prueba Gmail",
                "Mensaje de prueba");

        SmtpNotificationSender sender = new SmtpNotificationSender(
                new SmtpConfig("user@example.com", "secret", null, "smtp.example.com", 587, true, false));

        assertFalse(sender.supports(request));
    }
}
