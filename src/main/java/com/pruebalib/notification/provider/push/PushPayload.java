package com.pruebalib.notification.provider.push;

import java.util.Map;
import java.util.Objects;

public final class PushPayload {

    private final String recipient;
    private final String title;
    private final String body;
    private final Map<String, String> data;

    public PushPayload(String recipient, String title, String body, Map<String, String> data) {
        this.recipient = Objects.requireNonNull(recipient, "recipient no puede ser nulo");
        this.title = title == null ? "" : title;
        this.body = Objects.requireNonNull(body, "body no puede ser nulo");
        this.data = data == null ? Map.of() : Map.copyOf(data);
    }

    public String getRecipient() {
        return recipient;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getData() {
        return data;
    }
}
