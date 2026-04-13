package com.pruebalib.notification.provider.smtp;

import com.pruebalib.notification.api.NotificationRequest;

public final class SmtpRequestMapper {

    public SmtpPayload map(NotificationRequest request) {
        return new SmtpPayload(
                request.getRecipient(),
                request.getSubject(),
                request.getMessage());
    }
}
