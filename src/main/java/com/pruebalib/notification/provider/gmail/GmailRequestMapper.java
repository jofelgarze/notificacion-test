package com.pruebalib.notification.provider.gmail;

import com.pruebalib.notification.api.NotificationRequest;

public final class GmailRequestMapper {

    public GmailPayload map(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("NotificationRequest no puede ser nulo");
        }

        return new GmailPayload(
                request.getRecipient(),
                request.getSubject(),
                request.getMessage());
    }
}
