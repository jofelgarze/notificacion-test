package com.pruebalib.notification.core;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.spi.NotificationSender;

public abstract class AbstractNotificationSender implements NotificationSender {

    @Override
    public NotificationResult send(NotificationRequest request) {
        validateRequest(request);
        return doSend(request);
    }

    protected void validateRequest(NotificationRequest request) {
        Objects.requireNonNull(request, "el objeto request no debe ser nulo");
        Objects.requireNonNull(request.getDestination(), "destino no debe ser nulo");
        Objects.requireNonNull(request.getMessage(), "el mensaje no debe ser nulo");
    }

    protected abstract NotificationResult doSend(NotificationRequest request);
}