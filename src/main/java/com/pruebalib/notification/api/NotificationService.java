package com.pruebalib.notification.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.exception.UnsupportedChannelException;

public interface NotificationService {
    NotificationResult send(NotificationRequest request);

    CompletableFuture<NotificationResult> sendAsync(NotificationRequest request);

    default List<NotificationResult> sendBatch(List<NotificationRequest> requests) {
        if (requests == null) {
            throw new NotificationValidationException("requests no debe ser nulo");
        }

        return requests.stream()
                .map(this::send)
                .toList();
    }

    default CompletableFuture<List<NotificationResult>> sendBatchAsync(List<NotificationRequest> requests) {
        if (requests == null) {
            CompletableFuture<List<NotificationResult>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new NotificationValidationException("requests no debe ser nulo"));
            return failedFuture;
        }

        return CompletableFuture.supplyAsync(() -> sendBatch(requests));
    }

    default NotificationResult sendOrThrow(NotificationRequest request) {
        NotificationResult result = send(request);
        if (result.isSuccessful()) {
            return result;
        }

        String message = result.getTechnicalMessage() != null ? result.getTechnicalMessage() : result.getDescription();
        switch (result.getType()) {
            case VALIDATION_ERROR -> throw new NotificationValidationException(message);
            case CONFIGURATION_ERROR -> throw new NotificationConfigurationException(message);
            case UNSUPPORTED_CHANNEL -> throw new UnsupportedChannelException(message);
            case DELIVERY_ERROR -> throw new NotificationDeliveryException(message);
            case SUCCESS -> {
                return result;
            }
        }
        return result;
    }
}
