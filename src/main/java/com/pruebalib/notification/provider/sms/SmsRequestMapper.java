package com.pruebalib.notification.provider.sms;

import com.pruebalib.notification.api.NotificationRequest;

public final class SmsRequestMapper {

    public SmsPayload map(NotificationRequest request, SmsConfig config) {
        if (request == null) {
            throw new IllegalArgumentException("NotificationRequest no puede ser nulo");
        }
        if (config == null) {
            throw new IllegalArgumentException("SmsConfig no puede ser nulo");
        }

        return new SmsPayload(
                request.getRecipient(),
                request.getMessage(),
                config.from());
    }
}
