package com.pruebalib.notification.provider.push;

import java.util.UUID;

public class PushClient {

    public PushSendResponse send(PushPayload payload, PushConfig config) {
        if (payload == null) {
            throw new IllegalArgumentException("PushPayload no puede ser nulo");
        }
        if (config == null) {
            throw new IllegalArgumentException("PushConfig no puede ser nulo");
        }

        return new PushSendResponse(
                "push-" + UUID.randomUUID(),
                "accepted",
                true,
                null);
    }
}
