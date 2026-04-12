package com.pruebalib.notification.provider.gmail;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationMetadata;

public final class GmailConfig {

    private static final String USERNAME = "gmail.username";
    private static final String PASSWORD = "gmail.password";
    private static final String FROM = "gmail.from";
    private static final String HOST = "gmail.smtp.host";
    private static final String PORT = "gmail.smtp.port";
    private static final String STARTTLS = "gmail.smtp.starttls.enable";
    private static final String SSL = "gmail.smtp.ssl.enable";

    private final String username;
    private final String password;
    private final String from;
    private final String host;
    private final int port;
    private final boolean startTls;
    private final boolean ssl;

    private GmailConfig(
            String username,
            String password,
            String from,
            String host,
            int port,
            boolean startTls,
            boolean ssl) {
        this.username = username;
        this.password = password;
        this.from = from;
        this.host = host;
        this.port = port;
        this.startTls = startTls;
        this.ssl = ssl;
    }

    public static GmailConfig from(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("NotificationRequest no puede ser nulo");
        }

        NotificationMetadata metadata = request.getMetadata();
        if (metadata == null) {
            throw new IllegalArgumentException("Metadatos de notificación no pueden ser nulos");
        }

        String username = metadata.getString(USERNAME);
        String password = metadata.getString(PASSWORD);

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "El usuario de Gmail debe estar presente en metadata bajo '" + USERNAME + "'");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña de Gmail debe estar presente en metadata bajo '" + PASSWORD + "'");
        }

        String from = metadata.getString(FROM);
        if (from == null || from.isBlank()) {
            from = username;
        }

        String host = metadata.getString(HOST);
        if (host == null || host.isBlank()) {
            host = "smtp.gmail.com";
        }

        int port = parsePort(metadata.getString(PORT), 587);
        boolean startTls = parseBoolean(metadata.getString(STARTTLS), true);
        boolean ssl = parseBoolean(metadata.getString(SSL), false);

        return new GmailConfig(username, password, from, host, port, startTls, ssl);
    }

    private static int parsePort(String value, int defaultPort) {
        if (value == null || value.isBlank()) {
            return defaultPort;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El puerto de Gmail debe ser un número válido", e);
        }
    }

    private static boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
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
