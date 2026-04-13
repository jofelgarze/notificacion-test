package com.pruebalib.notification.core;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;

public final class NotificationEvent {

    private final NotificationEventType type;
    private final NotificationRequest request;
    private final NotificationResult result;
    private final String provider;
    private final String message;
    private final String trackerId;

    NotificationEvent(
            NotificationEventType type,
            NotificationRequest request,
            NotificationResult result,
            String provider,
            String message,
            String trackerId) {
        this.type = type;
        this.request = request;
        this.result = result;
        this.provider = provider;
        this.message = message;
        this.trackerId = trackerId;
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

    public String getTrackerId() {
        return trackerId;
    }
}
