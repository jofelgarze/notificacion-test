package com.pruebalib.notification.core;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

public class DefaultNotificationService implements NotificationService {

    private final NotificationSenderRegistry registry;
    private final Executor executor;

    public DefaultNotificationService(NotificationSenderRegistry registry, Executor executor) {
        this.registry = Objects.requireNonNull(registry, "NotificationSenderRegistry no debe ser nulo");
        this.executor = Objects.requireNonNull(executor, "exce cutor no debe ser nulo");
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        Objects.requireNonNull(request, "request no debe ser nulo");
        NotificationSender sender = registry.resolve(request);
        return sender.send(request);
    }

    @Override
    public CompletableFuture<NotificationResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }
}