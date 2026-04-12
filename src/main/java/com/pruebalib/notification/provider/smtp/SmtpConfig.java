package com.pruebalib.notification.provider.smtp;

public final class SmtpConfig {

    private final String username;
    private final String password;
    private final String from;
    private final String host;
    private final int port;
    private final boolean startTls;
    private final boolean ssl;

    public SmtpConfig(
            String username,
            String password,
            String from,
            String host,
            int port,
            boolean startTls,
            boolean ssl) {
        this.username = requireText(username, "El usuario SMTP no puede estar vacio");
        this.password = requireText(password, "La contrasena SMTP no puede estar vacia");
        this.host = requireText(host, "El host SMTP no puede estar vacio");
        this.port = requirePositivePort(port);
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

    private static int requirePositivePort(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("El puerto SMTP debe ser un numero valido");
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
