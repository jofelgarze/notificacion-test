package com.pruebalib.notification.core;

import java.util.List;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;

final class NotificationEventPublisher {

    private final List<NotificationListener> listeners;

    NotificationEventPublisher(List<NotificationListener> listeners) {
        this.listeners = listeners == null ? List.of() : List.copyOf(listeners);
    }

    void validationFailed(NotificationRequest request, NotificationResult result, String provider, String trackerId) {
        publish(NotificationEventType.VALIDATION_FAILED, request, result, provider, result.getDescription(), trackerId);
    }

    void sendStarted(NotificationRequest request, String provider, String trackerId) {
        publish(NotificationEventType.SEND_STARTED, request, null, provider,
                "Iniciando envio con provider " + provider, trackerId);
    }

    void sendSucceeded(NotificationRequest request, NotificationResult result, String provider, String trackerId) {
        publish(NotificationEventType.SEND_SUCCEEDED, request, result, provider, result.getDescription(), trackerId);
    }

    void sendFailed(NotificationRequest request, NotificationResult result, String provider, String trackerId) {
        publish(NotificationEventType.SEND_FAILED, request, result, provider, result.getDescription(), trackerId);
    }

    private void publish(
            NotificationEventType type,
            NotificationRequest request,
            NotificationResult result,
            String provider,
            String message,
            String trackerId) {
        NotificationEvent event = new NotificationEvent(type, request, result, provider, message, trackerId);
        for (NotificationListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
