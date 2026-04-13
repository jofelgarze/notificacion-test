package com.pruebalib.notification.provider.gmail;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class GmailNotificationSender extends AbstractNotificationSender<GmailConfig> {

    private static final String CHANNEL = "email";
    private static final String PROVIDER = "gmail";

    private final GmailRequestMapper mapper;
    private final GmailClient client;

    public GmailNotificationSender(GmailConfig config) {
        this(config, new GmailRequestMapper(), new GmailClient());
    }

    public GmailNotificationSender(GmailConfig config, GmailRequestMapper mapper, GmailClient client) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "GmailRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "GmailClient no debe ser nulo");
    }

    @Override
    public String channel() {
        return CHANNEL;
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    protected void validateRequest(NotificationRequest request) {
        Objects.requireNonNull(request, "el objeto request no debe ser nulo");
        Objects.requireNonNull(request.getChannel(), "channel no debe ser nulo");
        Objects.requireNonNull(request.getRecipient(), "recipient no debe ser nulo");
        Objects.requireNonNull(request.getMessage(), "el mensaje no debe ser nulo");
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            GmailPayload payload = mapper.map(request);
            GmailAuthenticator authenticator = GmailAuthenticator.from(getConfig());
            String providerMessageId = client.send(payload, authenticator, getConfig());
            return NotificationResult.success(providerMessageId, "Correo enviado con Gmail");
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure("Error en configuracion de Gmail: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure("Error al enviar correo Gmail: " + e.getMessage());
        }
    }
}
