package com.pruebalib.notification.provider.smtp;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;
import com.pruebalib.notification.core.NotificationAttributes;

public final class SmtpNotificationSender extends AbstractNotificationSender {

    private final SmtpRequestMapper mapper;
    private final SmtpClient client;

    public SmtpNotificationSender() {
        this.mapper = new SmtpRequestMapper();
        this.client = new SmtpClient();
    }

    @Override
    public boolean supports(NotificationRequest request) {
        if (request == null || request.getMetadata() == null) {
            return false;
        }

        String channel = request.getMetadata().getString(NotificationAttributes.CHANNEL);
        String provider = request.getMetadata().getString(NotificationAttributes.PROVIDER);

        return "email".equalsIgnoreCase(channel)
                && "smtp".equalsIgnoreCase(provider);
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            SmtpConfig config = SmtpConfig.from(request);
            SmtpPayload payload = mapper.map(request);
            SmtpAuthenticator authenticator = SmtpAuthenticator.from(config);
            String providerMessageId = client.send(payload, authenticator, config);
            return NotificationResult.success(providerMessageId, "Correo enviado vía SMTP genérico");
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure("Error en configuración SMTP: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure("Error al enviar correo SMTP: " + e.getMessage());
        }
    }
}
