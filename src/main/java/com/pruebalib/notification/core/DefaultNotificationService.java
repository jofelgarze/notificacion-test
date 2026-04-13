package com.pruebalib.notification.core;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

class DefaultNotificationService implements NotificationService {

    private final NotificationSenderRegistry registry;
    private final Executor executor;

    public DefaultNotificationService(NotificationSenderRegistry registry, Executor executor) {
        this.registry = Objects.requireNonNull(registry, "NotificationSenderRegistry no debe ser nulo");
        this.executor = Objects.requireNonNull(executor, "excecutor no debe ser nulo");
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        if (request == null) {
            return NotificationResult.validationError("request no debe ser nulo");
        }

        try {
            NotificationSender sender = registry.resolve(request);
            return sender.send(request);
        } catch (IllegalArgumentException e) {
            return NotificationResult.unsupportedChannel(e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.deliveryError("Error inesperado al enviar notificacion: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<NotificationResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }
}
