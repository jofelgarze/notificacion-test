package com.pruebalib.notification.api;

import java.util.Map;
import java.util.Objects;

public final class NotificationRequest {

    private final String channel;
    private final String recipient;
    private final String subject;
    private final String message;
    private final Map<String, String> data;

    public NotificationRequest(
            String channel,
            String recipient,
            String subject,
            String message) {
        this(channel, recipient, subject, message, null);
    }

    public NotificationRequest(
            String channel,
            String recipient,
            String subject,
            String message,
            Map<String, String> data) {
        this.channel = Objects.requireNonNull(channel, "channel must not be null");
        this.recipient = Objects.requireNonNull(recipient, "recipient must not be null");
        this.subject = subject;
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.data = data == null ? Map.of() : Map.copyOf(data);
    }

    public String getChannel() {
        return channel;
    }

    public String getTarget() {
        return channel;
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
