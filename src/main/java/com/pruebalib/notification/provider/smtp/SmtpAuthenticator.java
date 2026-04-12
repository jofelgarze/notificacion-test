package com.pruebalib.notification.provider.smtp;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public final class SmtpAuthenticator {

    private final String username;
    private final String password;

    private SmtpAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static SmtpAuthenticator from(SmtpConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("SmtpConfig no puede ser nulo");
        }
        if (config.username() == null || config.username().isBlank()) {
            throw new IllegalArgumentException("El usuario SMTP no puede estar vacío");
        }
        if (config.password() == null || config.password().isBlank()) {
            throw new IllegalArgumentException("La contraseña SMTP no puede estar vacía");
        }
        return new SmtpAuthenticator(config.username(), config.password());
    }

    public void validate() {
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Credenciales SMTP inválidas");
        }
    }

    public Authenticator toJavaMailAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }
}
