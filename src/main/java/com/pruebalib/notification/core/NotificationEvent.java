package com.pruebalib.notification.core;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;

final class NotificationEvent {

    private final NotificationEventType type;
    private final NotificationRequest request;
    private final NotificationResult result;
    private final String provider;
    private final String message;

    NotificationEvent(
            NotificationEventType type,
            NotificationRequest request,
            NotificationResult result,
            String provider,
            String message) {
        this.type = type;
        this.request = request;
        this.result = result;
        this.provider = provider;
        this.message = message;
    }

    public NotificationEventType getType() {
        return type;
    }

    public NotificationRequest getRequest() {
        return request;
    }

    public NotificationResult getResult() {
        return result;
    }

    public String getProvider() {
        return provider;
    }

    public String getMessage() {
        return message;
    }
}
