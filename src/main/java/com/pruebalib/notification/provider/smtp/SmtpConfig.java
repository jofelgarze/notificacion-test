package com.pruebalib.notification.provider.smtp;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationMetadata;

public final class SmtpConfig {

    private static final String USERNAME = "smtp.username";
    private static final String PASSWORD = "smtp.password";
    private static final String FROM = "smtp.from";
    private static final String HOST = "smtp.host";
    private static final String PORT = "smtp.port";
    private static final String STARTTLS = "smtp.starttls.enable";
    private static final String SSL = "smtp.ssl.enable";

    private final String username;
    private final String password;
    private final String from;
    private final String host;
    private final int port;
    private final boolean startTls;
    private final boolean ssl;

    private SmtpConfig(
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

    public static SmtpConfig from(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("NotificationRequest no puede ser nulo");
        }

        NotificationMetadata metadata = request.getMetadata();
        if (metadata == null) {
            throw new IllegalArgumentException("Metadatos de notificación no pueden ser nulos");
        }

        String username = metadata.getString(USERNAME);
        String password = metadata.getString(PASSWORD);
        String host = metadata.getString(HOST);
        String portValue = metadata.getString(PORT);

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "El usuario SMTP debe estar presente en metadata bajo '" + USERNAME + "'");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña SMTP debe estar presente en metadata bajo '" + PASSWORD + "'");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException(
                    "El host SMTP debe estar presente en metadata bajo '" + HOST + "'");
        }
        if (portValue == null || portValue.isBlank()) {
            throw new IllegalArgumentException(
                    "El puerto SMTP debe estar presente en metadata bajo '" + PORT + "'");
        }

        int port = parsePort(portValue);
        boolean startTls = parseBoolean(metadata.getString(STARTTLS), false);
        boolean ssl = parseBoolean(metadata.getString(SSL), false);
        String from = metadata.getString(FROM);
        if (from == null || from.isBlank()) {
            from = username;
        }

        return new SmtpConfig(username, password, from, host, port, startTls, ssl);
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El puerto SMTP debe ser un número válido", e);
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
