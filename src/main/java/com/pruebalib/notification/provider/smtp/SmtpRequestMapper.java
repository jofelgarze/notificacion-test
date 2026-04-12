package com.pruebalib.notification.provider.smtp;

import com.pruebalib.notification.api.NotificationRequest;

public final class SmtpRequestMapper {

    public SmtpPayload map(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("NotificationRequest no puede ser nulo");
        }

        return new SmtpPayload(
                request.getDestination(),
                request.getSubject(),
                request.getMessage());
    }
}
