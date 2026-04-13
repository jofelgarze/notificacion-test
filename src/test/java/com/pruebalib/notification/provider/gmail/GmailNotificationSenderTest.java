package com.pruebalib.notification.provider.gmail;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GmailNotificationSenderTest {

        @Test
        void shouldSupportSendGmailRequests() {
                NotificationRequest request = new NotificationRequest(
                                "email",
                                "detination@gmail.com",
                                "Prueba Gmail",
                                "Mensaje de prueba");

                GmailNotificationSender sender = new GmailNotificationSender(
                                new GmailConfig("test@gmail.com", "secret123", "test@gmail.com", null,
                                                465, false,
                                                true));

                assertTrue(sender.supports(request));

                NotificationResult result = sender.send(request);

                assertNotNull(result);
                assertNotNull(result.getDescription());
        }

        @Test
        void shouldNotSupportNonGmailRequests() {
                NotificationRequest request = new NotificationRequest(
                                "push",
                                "dest@example.com",
                                "Prueba Push",
                                "Mensaje de prueba");

                GmailNotificationSender sender = new GmailNotificationSender(
                                new GmailConfig("user@gmail.com", "secret", null, null, 587, true, false));

                assertFalse(sender.supports(request));
        }
}
