package com.pruebalib.notification.provider.push;

import com.pruebalib.notification.api.NotificationRequest;

public final class PushRequestMapper {

    public PushPayload map(NotificationRequest request) {
        return new PushPayload(
                request.getRecipient(),
                request.getSubject(),
                request.getMessage(),
                request.getData());
    }
}
