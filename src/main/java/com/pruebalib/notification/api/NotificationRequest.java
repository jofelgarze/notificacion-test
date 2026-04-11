package com.pruebalib.notification.api;

import java.util.Objects;

public final class NotificationRequest {

    private final String destination;
    private final String subject;
    private final String message;
    private final NotificationMetadata metadata;

    public NotificationRequest(
            String destination,
            String subject,
            String message,
            NotificationMetadata metadata) {
        this.destination = Objects.requireNonNull(destination, "destination must not be null");
        this.subject = subject;
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.metadata = metadata == null ? new NotificationMetadata(null) : metadata;
    }

    public String getDestination() {
        return destination;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public NotificationMetadata getMetadata() {
        return metadata;
    }
}