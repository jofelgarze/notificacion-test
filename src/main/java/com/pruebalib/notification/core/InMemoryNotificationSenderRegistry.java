package com.pruebalib.notification.core;

import java.util.List;
import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

public class InMemoryNotificationSenderRegistry implements NotificationSenderRegistry {

    private final List<NotificationSender> senders;

    public InMemoryNotificationSenderRegistry(List<NotificationSender> senders) {
        this.senders = List.copyOf(Objects.requireNonNull(senders, "proveedores no debe ser nulo"));
    }

    @Override
    public NotificationSender resolve(NotificationRequest request) {
        return senders.stream()
                .filter(sender -> sender.supports(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro sender compatible con target: " + request.getTarget()));
    }
}
