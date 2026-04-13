package com.pruebalib.notification.provider.push;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushNotificationSenderTest {

    @Test
    void shouldSupportPushTarget() {
        NotificationRequest request = new NotificationRequest(
                "push",
                "device-token-123",
                "Titulo",
                "Mensaje",
                Map.of("screen", "offers"));

        PushNotificationSender sender = new PushNotificationSender(validConfig());

        assertTrue(sender.supports(request));
    }

    @Test
    void shouldNotSupportIncorrectTarget() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "device-token-123",
                "Titulo",
                "Mensaje",
                Map.of("screen", "offers"));

        PushNotificationSender sender = new PushNotificationSender(validConfig());

        assertFalse(sender.supports(request));
    }

    @Test
    void shouldBuildPayloadAndInvokeClientWithExpectedData() {
        NotificationRequest request = new NotificationRequest(
                "push",
                "device-token-123",
                "Nueva promo",
                "Tienes un descuento disponible",
                Map.of("campaign", "apr-2026", "screen", "offers"));

        CapturingPushClient client = new CapturingPushClient(new PushSendResponse(
                "push-abc-123",
                "accepted",
                true,
                null));

        PushConfig config = validConfig();
        PushNotificationSender sender = new PushNotificationSender(config, new PushRequestMapper(), client);

        NotificationResult result = sender.send(request);

        assertTrue(client.invoked);
        assertSame(config, client.capturedConfig);
        assertNotNull(client.capturedPayload);
        assertEquals("device-token-123", client.capturedPayload.getRecipient());
        assertEquals("Nueva promo", client.capturedPayload.getTitle());
        assertEquals("Tienes un descuento disponible", client.capturedPayload.getBody());
        assertEquals(Map.of("campaign", "apr-2026", "screen", "offers"), client.capturedPayload.getData());
        assertTrue(result.isSuccessful());
        assertEquals(NotificationResultType.SUCCESS, result.getType());
        assertEquals("push-abc-123", result.getProviderMessageId());
        assertTrue(result.getDescription().contains("accepted"));
    }

    @Test
    void shouldInterpretProviderStyleSuccessResponse() {
        NotificationRequest request = new NotificationRequest(
                "push",
                "topic:news",
                "Ultima hora",
                "Se publico una actualizacion",
                Map.of("severity", "high"));

        PushClient client = new CapturingPushClient(new PushSendResponse(
                "projects/demo/messages/0:1234567890",
                "queued",
                true,
                null));

        PushNotificationSender sender = new PushNotificationSender(validConfig(), new PushRequestMapper(), client);

        NotificationResult result = sender.send(request);

        assertTrue(result.isSuccessful());
        assertEquals(NotificationResultType.SUCCESS, result.getType());
        assertEquals("projects/demo/messages/0:1234567890", result.getProviderMessageId());
        assertTrue(result.getDescription().contains("queued"));
    }

    @Test
    void shouldReturnFailureForControlledProviderFailure() {
        NotificationRequest request = new NotificationRequest(
                "push",
                "device-token-invalid",
                "Titulo",
                "Mensaje",
                Map.of("screen", "offers"));

        PushClient client = new CapturingPushClient(new PushSendResponse(
                null,
                "rejected",
                false,
                "Invalid device token"));

        PushNotificationSender sender = new PushNotificationSender(validConfig(), new PushRequestMapper(), client);

        NotificationResult result = sender.send(request);

        assertFalse(result.isSuccessful());
        assertEquals(NotificationResultType.DELIVERY_ERROR, result.getType());
        assertEquals(null, result.getProviderMessageId());
        assertTrue(result.getDescription().contains("rejected"));
        assertTrue(result.getDescription().contains("Invalid device token"));
    }

    private static PushConfig validConfig() {
        return new PushConfig("project-demo", "token-demo", "https://api.push-provider.local");
    }

    private static final class CapturingPushClient extends PushClient {
        private final PushSendResponse response;
        private boolean invoked;
        private PushPayload capturedPayload;
        private PushConfig capturedConfig;

        private CapturingPushClient(PushSendResponse response) {
            this.response = response;
        }

        @Override
        public PushSendResponse send(PushPayload payload, PushConfig config) {
            this.invoked = true;
            this.capturedPayload = payload;
            this.capturedConfig = config;
            return response;
        }
    }
}
