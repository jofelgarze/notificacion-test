package com.pruebalib.notification.core;

import java.util.List;
import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.common.exception.UnsupportedChannelException;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

class InMemoryNotificationSenderRegistry implements NotificationSenderRegistry {

    private final List<NotificationSender> senders;

    public InMemoryNotificationSenderRegistry(List<NotificationSender> senders) {
        this.senders = List.copyOf(Objects.requireNonNull(senders, "proveedores no debe ser nulo"));
    }

    @Override
    public NotificationSender resolve(NotificationRequest request) {
        return resolveAll(request).stream()
                .findFirst()
                .orElseThrow(() -> new UnsupportedChannelException(
                        "No se encontro sender compatible con channel: " + request.getChannel()));
    }

    @Override
    public List<NotificationSender> resolveAll(NotificationRequest request) {
        return senders.stream()
                .filter(sender -> sender.supports(request))
                .toList();
    }
}
