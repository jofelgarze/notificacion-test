package com.pruebalib.notification.provider.gmail;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class GmailNotificationSender extends AbstractNotificationSender {

    private static final String TARGET = "gmail";

    private final GmailConfig config;
    private final GmailRequestMapper mapper;
    private final GmailClient client;

    public GmailNotificationSender(GmailConfig config) {
        this(config, new GmailRequestMapper(), new GmailClient());
    }

    public GmailNotificationSender(GmailConfig config, GmailRequestMapper mapper, GmailClient client) {
        this.config = Objects.requireNonNull(config, "GmailConfig no debe ser nulo");
        this.mapper = Objects.requireNonNull(mapper, "GmailRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "GmailClient no debe ser nulo");
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
            GmailPayload payload = mapper.map(request);
            GmailAuthenticator authenticator = GmailAuthenticator.from(config);
            String providerMessageId = client.send(payload, authenticator, config);
            return NotificationResult.success(providerMessageId, "Correo enviado con Gmail");
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure("Error en configuracion de Gmail: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure("Error al enviar correo Gmail: " + e.getMessage());
        }
    }
}
