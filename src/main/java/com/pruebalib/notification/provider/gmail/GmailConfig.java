package com.pruebalib.notification.provider.gmail;

import com.pruebalib.notification.spi.NotificationSenderConfig;

public final class GmailConfig implements NotificationSenderConfig {

    private final String username;
    private final String password;
    private final String from;
    private final String host;
    private final int port;
    private final boolean startTls;
    private final boolean ssl;

    public GmailConfig(
            String username,
            String password,
            String from,
            String host,
            int port,
            boolean startTls,
            boolean ssl) {
        this.username = requireText(username, "El usuario de Gmail no puede estar vacio");
        this.password = requireText(password, "La contrasena de Gmail no puede estar vacia");
        this.host = host == null || host.isBlank() ? "smtp.gmail.com" : host;
        this.port = port <= 0 ? 587 : port;
        this.startTls = startTls;
        this.ssl = ssl;
        this.from = from == null || from.isBlank() ? this.username : from;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String from() {
        return from;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public boolean startTls() {
        return startTls;
    }

    public boolean ssl() {
        return ssl;
    }
}
