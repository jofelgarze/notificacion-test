package com.pruebalib.notification.api;

import java.util.Map;
import java.util.Objects;

public final class NotificationRequest {

    private final String target;
    private final String recipient;
    private final String subject;
    private final String message;
    private final Map<String, String> data;

    public NotificationRequest(
            String target,
            String recipient,
            String subject,
            String message) {
        this(target, recipient, subject, message, null);
    }

    public NotificationRequest(
            String target,
            String recipient,
            String subject,
            String message,
            Map<String, String> data) {
        this.target = Objects.requireNonNull(target, "target must not be null");
        this.recipient = Objects.requireNonNull(recipient, "recipient must not be null");
        this.subject = subject;
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.data = data == null ? Map.of() : Map.copyOf(data);
    }

    public String getTarget() {
        return target;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getData() {
        return data;
    }
}
