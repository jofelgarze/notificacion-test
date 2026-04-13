package com.pruebalib.notification.core;

import java.util.List;
import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.spi.NotificationSender;

final class DefaultNotificationRoutingPolicy implements NotificationRoutingPolicy {

    @Override
    public List<NotificationSender> order(NotificationRequest request, List<NotificationSender> candidates) {
        Objects.requireNonNull(request, "request no debe ser nulo");
        Objects.requireNonNull(candidates, "candidates no debe ser nulo");
        return List.copyOf(candidates);
    }
}
