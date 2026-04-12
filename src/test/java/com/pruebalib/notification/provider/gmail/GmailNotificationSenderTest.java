package com.pruebalib.notification.provider.gmail;

import com.pruebalib.notification.api.NotificationMetadata;
import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.NotificationAttributes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GmailNotificationSenderTest {

        @Test
        void shouldSupportGmailEmailRequests() {
                NotificationMetadata metadata = new NotificationMetadata(Map.of(
                                NotificationAttributes.CHANNEL, "email",
                                NotificationAttributes.PROVIDER, "gmail",
                                "gmail.username", "test@gmail.com",
                                "gmail.password", "secret123",
                                "mail.smtp.port", "465",
                                "mail.smtp.ssl.enable", "false"));

                NotificationRequest request = new NotificationRequest(
                                "jofelgarze@gmail.com",
                                "Prueba Gmail",
                                "Mensaje de prueba 2",
                                metadata);

                GmailNotificationSender sender = new GmailNotificationSender();
                NotificationResult result = sender.send(request);

                assertNotNull(result);
                assertTrue(result.isSuccessful());
                assertNotNull(result.getDescription());
                assertFalse(result.getDescription().toLowerCase().contains("error"));
        }

        @Test
        void shouldNotSupportNonGmailRequests() {
                NotificationMetadata metadata = new NotificationMetadata(Map.of(
                                NotificationAttributes.CHANNEL, "email",
                                NotificationAttributes.PROVIDER, "smtp",
                                "smtp.username", "user@example.com",
                                "smtp.password", "secret"));

                NotificationRequest request = new NotificationRequest(
                                "destino@example.com",
                                "Prueba SMTP",
                                "Mensaje de prueba",
                                metadata);

                GmailNotificationSender sender = new GmailNotificationSender();

                assertFalse(sender.supports(request));
        }
}
