package com.pruebalib.notification.core;

import java.util.List;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;

final class NotificationEventPublisher {

    private final List<NotificationListener> listeners;

    NotificationEventPublisher(List<NotificationListener> listeners) {
        this.listeners = listeners == null ? List.of() : List.copyOf(listeners);
    }

    void validationFailed(NotificationRequest request, NotificationResult result, String provider) {
        publish(NotificationEventType.VALIDATION_FAILED, request, result, provider, result.getDescription());
    }

    void sendStarted(NotificationRequest request, String provider) {
        publish(NotificationEventType.SEND_STARTED, request, null, provider,
                "Iniciando envio con provider " + provider);
    }

    void sendSucceeded(NotificationRequest request, NotificationResult result, String provider) {
        publish(NotificationEventType.SEND_SUCCEEDED, request, result, provider, result.getDescription());
    }

    void sendFailed(NotificationRequest request, NotificationResult result, String provider) {
        publish(NotificationEventType.SEND_FAILED, request, result, provider, result.getDescription());
    }

    private void publish(
            NotificationEventType type,
            NotificationRequest request,
            NotificationResult result,
            String provider,
            String message) {
        NotificationEvent event = new NotificationEvent(type, request, result, provider, message);
        for (NotificationListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
