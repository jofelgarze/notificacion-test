package com.pruebalib.notification.provider.push;

import java.util.UUID;

import com.pruebalib.notification.common.exception.NotificationConfigurationException;

class PushClient {

    public PushSendResponse send(PushPayload payload, PushConfig config) {
        if (payload == null) {
            throw new NotificationConfigurationException("PushPayload no puede ser nulo");
        }
        if (config == null) {
            throw new NotificationConfigurationException("PushConfig no puede ser nulo");
        }

        return new PushSendResponse(
                "push-" + UUID.randomUUID(),
                "accepted",
                true,
                null);
    }
}
