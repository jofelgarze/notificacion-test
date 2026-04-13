package com.pruebalib.notification.provider.gmail;

import java.util.Objects;

final class GmailPayload {

    private final String recipient;
    private final String subject;
    private final String body;

    public GmailPayload(String recipient, String subject, String body) {
        this.recipient = Objects.requireNonNull(recipient, "recipient no puede ser nulo");
        this.subject = subject == null ? "" : subject;
        this.body = Objects.requireNonNull(body, "body no puede ser nulo");
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
