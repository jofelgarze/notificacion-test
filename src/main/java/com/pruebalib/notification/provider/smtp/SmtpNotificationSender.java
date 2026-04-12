package com.pruebalib.notification.provider.smtp;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class SmtpNotificationSender extends AbstractNotificationSender {

    private static final String TARGET = "smtp";

    private final SmtpConfig config;
    private final SmtpRequestMapper mapper;
    private final SmtpClient client;

    public SmtpNotificationSender(SmtpConfig config) {
        this(config, new SmtpRequestMapper(), new SmtpClient());
    }

    public SmtpNotificationSender(SmtpConfig config, SmtpRequestMapper mapper, SmtpClient client) {
        this.config = Objects.requireNonNull(config, "SmtpConfig no debe ser nulo");
        this.mapper = Objects.requireNonNull(mapper, "SmtpRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "SmtpClient no debe ser nulo");
    }

    @Override
    public boolean supports(NotificationRequest request) {
        if (request == null || request.getTarget() == null) {
            return false;
        }
        return TARGET.equalsIgnoreCase(request.getTarget());
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            SmtpPayload payload = mapper.map(request);
            SmtpAuthenticator authenticator = SmtpAuthenticator.from(config);
            String providerMessageId = client.send(payload, authenticator, config);
            return NotificationResult.success(providerMessageId, "Correo enviado via SMTP");
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure("Error en configuracion SMTP: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure("Error al enviar correo SMTP: " + e.getMessage());
        }
    }
}
