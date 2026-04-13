package com.pruebalib.notification.provider.sms;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;

final class SmsRequestMapper {

    public SmsPayload map(NotificationRequest request, SmsConfig config) {
        if (config == null) {
            throw new NotificationConfigurationException("SmsConfig no puede ser nulo");
        }

        return new SmsPayload(
                request.getRecipient(),
                request.getMessage(),
                config.from());
    }
}
