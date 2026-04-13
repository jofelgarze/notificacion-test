package com.pruebalib.notification.provider.smtp;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

import com.pruebalib.notification.common.exception.NotificationConfigurationException;

final class SmtpAuthenticator {

    private final String username;
    private final String password;

    private SmtpAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static SmtpAuthenticator from(SmtpConfig config) {
        if (config == null) {
            throw new NotificationConfigurationException("SmtpConfig no puede ser nulo");
        }
        if (config.username() == null || config.username().isBlank()) {
            throw new NotificationConfigurationException("El usuario SMTP no puede estar vacio");
        }
        if (config.password() == null || config.password().isBlank()) {
            throw new NotificationConfigurationException("La contrasena SMTP no puede estar vacia");
        }
        return new SmtpAuthenticator(config.username(), config.password());
    }

    public void validate() {
        if (username.isBlank() || password.isBlank()) {
            throw new NotificationConfigurationException("Credenciales SMTP invalidas");
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
