package com.pruebalib.notification.api;

import java.util.Objects;

public final class NotificationRequest {

    private final String target;
    private final String recipient;
    private final String subject;
    private final String message;

    public NotificationRequest(
            String target,
            String recipient,
            String subject,
            String message) {
        this.target = Objects.requireNonNull(target, "target must not be null");
        this.recipient = Objects.requireNonNull(recipient, "recipient must not be null");
        this.subject = subject;
        this.message = Objects.requireNonNull(message, "message must not be null");
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
}
