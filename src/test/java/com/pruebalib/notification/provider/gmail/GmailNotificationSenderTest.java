package com.pruebalib.notification.provider.gmail;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GmailNotificationSenderTest {

        @Test
        void shouldBuildPayloadAndInvokeClientWithExpectedData() {
                NotificationRequest request = new NotificationRequest(
                                "email",
                                "destination@gmail.com",
                                "Prueba Gmail",
                                "Mensaje de prueba");

                GmailConfig config = new GmailConfig("test@gmail.com", "secret123", "test@gmail.com", null,
                                465, false, true);
                CapturingGmailClient client = new CapturingGmailClient("gmail-123");
                GmailNotificationSender sender = new GmailNotificationSender(config, new GmailRequestMapper(), client);

                assertTrue(sender.supports(request));

                NotificationResult result = sender.send(request);

                assertTrue(client.invoked);
                assertSame(config, client.capturedConfig);
                assertNotNull(client.capturedPayload);
                assertEquals("destination@gmail.com", client.capturedPayload.getRecipient());
                assertEquals("Prueba Gmail", client.capturedPayload.getSubject());
                assertEquals("Mensaje de prueba", client.capturedPayload.getBody());
                assertNotNull(result);
                assertTrue(result.isSuccessful());
                assertEquals(NotificationResultType.SUCCESS, result.getType());
                assertEquals("gmail-123", result.getProviderMessageId());
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

        private static final class CapturingGmailClient extends GmailClient {
                private final String response;
                private boolean invoked;
                private GmailPayload capturedPayload;
                private GmailConfig capturedConfig;

                private CapturingGmailClient(String response) {
                        this.response = response;
                }

                @Override
                public String send(GmailPayload payload, GmailAuthenticator authenticator, GmailConfig config) {
                        this.invoked = true;
                        this.capturedPayload = payload;
                        this.capturedConfig = config;
                        return response;
                }
        }
}
