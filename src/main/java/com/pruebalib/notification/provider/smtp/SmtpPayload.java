package com.pruebalib.notification.provider.smtp;

import java.util.Objects;

public final class SmtpPayload {

    private final String recipient;
    private final String subject;
    private final String body;

    public SmtpPayload(String recipient, String subject, String body) {
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
