package com.pruebalib.notification.provider.smtp;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmtpNotificationSenderTest {

    @Test
    void shouldBuildPayloadAndInvokeClientWithExpectedData() {
        NotificationRequest request = new NotificationRequest(
                "email",
                "dest@example.com",
                "Prueba SMTP",
                "Mensaje de prueba");

        SmtpConfig config = new SmtpConfig("user@example.com", "secret", null, "smtp.example.com", 587, true, false);
        CapturingSmtpClient client = new CapturingSmtpClient("smtp-123");
        SmtpNotificationSender sender = new SmtpNotificationSender(config, new SmtpRequestMapper(), client);

        assertTrue(sender.supports(request));

        NotificationResult result = sender.send(request);

        assertTrue(client.invoked);
        assertSame(config, client.capturedConfig);
        assertNotNull(client.capturedPayload);
        assertEquals("dest@example.com", client.capturedPayload.getRecipient());
        assertEquals("Prueba SMTP", client.capturedPayload.getSubject());
        assertEquals("Mensaje de prueba", client.capturedPayload.getBody());
        assertTrue(result.isSuccessful());
        assertEquals("smtp-123", result.getProviderMessageId());
    }

    @Test
    void shouldNotSupportNonSmtpRequests() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Mensaje de prueba");

        SmtpNotificationSender sender = new SmtpNotificationSender(
                new SmtpConfig("user@example.com", "secret", null, "smtp.example.com", 587, true, false));

        assertFalse(sender.supports(request));
    }

    private static final class CapturingSmtpClient extends SmtpClient {
        private final String response;
        private boolean invoked;
        private SmtpPayload capturedPayload;
        private SmtpConfig capturedConfig;

        private CapturingSmtpClient(String response) {
            this.response = response;
        }

        @Override
        public String send(SmtpPayload payload, SmtpAuthenticator authenticator, SmtpConfig config) {
            this.invoked = true;
            this.capturedPayload = payload;
            this.capturedConfig = config;
            return response;
        }
    }
}
