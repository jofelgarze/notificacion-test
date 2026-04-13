package com.pruebalib.notification.core;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

public final class NotificationServiceFactory {

    private NotificationServiceFactory() {
    }

    public static NotificationService create(List<NotificationSender> senders, Executor executor) {
        NotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                Objects.requireNonNull(senders, "senders no debe ser nulo"));
        return new DefaultNotificationService(registry, executor);
    }

    public static NotificationService create(List<NotificationSender> senders) {
        return create(senders, Runnable::run);
    }
}
