package com.pruebalib.notification.provider.gmail;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;
import com.pruebalib.notification.core.NotificationAttributes;

public final class GmailNotificationSender extends AbstractNotificationSender {

    private final GmailRequestMapper mapper;
    private final GmailClient client;

    public GmailNotificationSender() {
        this.mapper = new GmailRequestMapper();
        this.client = new GmailClient();
    }

    @Override
    public boolean supports(NotificationRequest request) {
        if (request == null || request.getMetadata() == null) {
            return false;
        }

        String channel = request.getMetadata().getString(NotificationAttributes.CHANNEL);
        String provider = request.getMetadata().getString(NotificationAttributes.PROVIDER);

        return "email".equalsIgnoreCase(channel)
                && "gmail".equalsIgnoreCase(provider);
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            GmailConfig config = GmailConfig.from(request);
            GmailPayload payload = mapper.map(request);
            GmailAuthenticator authenticator = GmailAuthenticator.from(config);
            String providerMessageId = client.send(payload, authenticator, config);
            return NotificationResult.success(providerMessageId, "Correo enviado con Gmail");
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure("Error en configuración de Gmail: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure("Error al enviar correo Gmail: " + e.getMessage());
        }
    }
}
