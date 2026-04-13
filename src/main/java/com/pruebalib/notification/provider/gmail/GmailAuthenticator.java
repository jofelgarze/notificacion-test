package com.pruebalib.notification.provider.gmail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

import com.pruebalib.notification.common.exception.NotificationConfigurationException;

final class GmailAuthenticator {

    private final String username;
    private final String password;

    private GmailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static GmailAuthenticator from(GmailConfig config) {
        if (config == null) {
            throw new NotificationConfigurationException("GmailConfig no puede ser nulo");
        }
        if (config.username() == null || config.username().isBlank()) {
            throw new NotificationConfigurationException("El usuario de Gmail no puede estar vacio");
        }
        if (config.password() == null || config.password().isBlank()) {
            throw new NotificationConfigurationException("La contrasena de Gmail no puede estar vacia");
        }
        return new GmailAuthenticator(config.username(), config.password());
    }

    public void validate() {
        if (username.isBlank() || password.isBlank()) {
            throw new NotificationConfigurationException("Credenciales de Gmail invalidas");
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

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
