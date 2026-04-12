package com.pruebalib.notification.provider.sms;

import java.time.Instant;
import java.util.UUID;

public class SmsClient {

    public SmsSendResponse send(SmsPayload payload, SmsConfig config) {
        if (payload == null) {
            throw new IllegalArgumentException("SmsPayload no puede ser nulo");
        }
        if (config == null) {
            throw new IllegalArgumentException("SmsConfig no puede ser nulo");
        }

        return new SmsSendResponse(
                "sms-" + UUID.randomUUID(),
                "queued",
                null,
                null,
                Instant.now(),
                calculateSegmentCount(payload.getMessage()));
    }

    protected int calculateSegmentCount(String message) {
        if (message == null || message.isBlank()) {
            return 0;
        }
        return Math.max(1, (int) Math.ceil(message.length() / 160.0d));
    }
}
