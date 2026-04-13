package com.pruebalib.notification.core.validation;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.common.exception.NotificationValidationException;

public final class DefaultNotificationRequestValidator implements NotificationRequestValidator {

    @Override
    public void validate(NotificationRequest request) {
        if (request == null) {
            throw new NotificationValidationException("el objeto request no debe ser nulo");
        }
        requireText(request.getChannel(), "channel no debe ser nulo");
        requireText(request.getRecipient(), "recipient no debe ser nulo");
        requireText(request.getMessage(), "el mensaje no debe ser nulo");
    }

    private void requireText(String value, String message) {
        Objects.requireNonNull(value, message);
    }
}
