package com.pruebalib.notification.provider.sms;

import java.util.Objects;

final class SmsPayload {

    private final String recipient;
    private final String message;
    private final String from;

    public SmsPayload(String recipient, String message, String from) {
        this.recipient = Objects.requireNonNull(recipient, "recipient no puede ser nulo");
        this.message = Objects.requireNonNull(message, "message no puede ser nulo");
        this.from = Objects.requireNonNull(from, "from no puede ser nulo");
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }
}
