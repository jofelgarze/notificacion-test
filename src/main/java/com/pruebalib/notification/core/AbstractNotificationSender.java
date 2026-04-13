package com.pruebalib.notification.core;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderConfig;

public abstract class AbstractNotificationSender<C extends NotificationSenderConfig> implements NotificationSender {

    private final C config;

    protected AbstractNotificationSender(C config) {
        this.config = Objects.requireNonNull(config, "config no debe ser nulo");
    }

    @Override
    public boolean supports(NotificationRequest request) {
        return request != null
                && request.getChannel() != null
                && channel().equalsIgnoreCase(request.getChannel());
    }

    @Override
    public final NotificationResult send(NotificationRequest request) {
        validateRequest(request);
        return doSend(request);
    }

    protected final C getConfig() {
        return config;
    }

    protected final void requireRequest(NotificationRequest request) {
        Objects.requireNonNull(request, "el objeto request no debe ser nulo");
    }

    protected final void requireChannel(NotificationRequest request) {
        Objects.requireNonNull(request.getChannel(), "channel no debe ser nulo");
    }

    protected final void requireRecipient(NotificationRequest request) {
        Objects.requireNonNull(request.getRecipient(), "recipient no debe ser nulo");
    }

    protected final void requireMessage(NotificationRequest request) {
        Objects.requireNonNull(request.getMessage(), "el mensaje no debe ser nulo");
    }

    protected abstract void validateRequest(NotificationRequest request);

    protected abstract NotificationResult doSend(NotificationRequest request);
}
