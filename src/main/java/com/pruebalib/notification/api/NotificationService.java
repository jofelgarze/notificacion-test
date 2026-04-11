package com.pruebalib.notification.api;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    NotificationResult send(NotificationRequest request);

    CompletableFuture<NotificationResult> sendAsync(NotificationRequest request);
}