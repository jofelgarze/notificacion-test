package com.pruebalib.notification.provider.sms;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmsNotificationSenderTest {

    @Test
    void shouldBuildPayloadAndInvokeClientWithExpectedData() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                "Ignorado",
                "Mensaje corto");

        CapturingSmsClient client = new CapturingSmsClient(new SmsSendResponse(
                "SM123456789",
                "queued",
                null,
                null,
                Instant.parse("2026-04-12T10:15:30Z"),
                1));

        SmsConfig config = validConfig();
        SmsNotificationSender sender = new SmsNotificationSender(config, new SmsRequestMapper(), client);

        NotificationResult result = sender.send(request);

        assertTrue(client.invoked);
        assertSame(config, client.capturedConfig);
        assertNotNull(client.capturedPayload);
        assertEquals("+593999999999", client.capturedPayload.getRecipient());
        assertEquals("Mensaje corto", client.capturedPayload.getMessage());
        assertEquals("+15005550006", client.capturedPayload.getFrom());
        assertTrue(result.isSuccessful());
        assertEquals(NotificationResultType.SUCCESS, result.getType());
        assertEquals("SM123456789", result.getProviderMessageId());
        assertTrue(result.getDescription().contains("queued"));
    }

    @Test
    void shouldInterpretProviderStyleSuccessResponse() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Mensaje de confirmacion");

        SmsClient client = new CapturingSmsClient(new SmsSendResponse(
                "SMaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "accepted",
                null,
                null,
                Instant.parse("2026-04-12T10:15:30Z"),
                2));

        SmsNotificationSender sender = new SmsNotificationSender(validConfig(), new SmsRequestMapper(), client);

        NotificationResult result = sender.send(request);

        assertTrue(result.isSuccessful());
        assertEquals(NotificationResultType.SUCCESS, result.getType());
        assertEquals("SMaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", result.getProviderMessageId());
        assertTrue(result.getDescription().contains("accepted"));
        assertTrue(result.getDescription().contains("2 segmento(s)"));
    }

    @Test
    void shouldReturnFailureForControlledProviderFailure() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Mensaje bloqueado");

        SmsClient client = new CapturingSmsClient(new SmsSendResponse(
                "SM999999999",
                "failed",
                "30007",
                "Message filtered",
                Instant.parse("2026-04-12T10:15:30Z"),
                1));

        SmsNotificationSender sender = new SmsNotificationSender(validConfig(), new SmsRequestMapper(), client);

        NotificationResult result = sender.send(request);

        assertTrue(!result.isSuccessful());
        assertEquals(NotificationResultType.DELIVERY_ERROR, result.getType());
        assertEquals(null, result.getProviderMessageId());
        assertEquals("30007", result.getErrorCode());
        assertTrue(result.getDescription().contains("failed"));
        assertTrue(result.getDescription().contains("Message filtered"));
    }

    private static SmsConfig validConfig() {
        return new SmsConfig("acct-123", "token-xyz", "+15005550006", "https://api.sms-provider.local");
    }

    private static final class CapturingSmsClient extends SmsClient {
        private final SmsSendResponse response;
        private boolean invoked;
        private SmsPayload capturedPayload;
        private SmsConfig capturedConfig;

        private CapturingSmsClient(SmsSendResponse response) {
            this.response = response;
        }

        @Override
        public SmsSendResponse send(SmsPayload payload, SmsConfig config) {
            this.invoked = true;
            this.capturedPayload = payload;
            this.capturedConfig = config;
            return response;
        }
    }
}
