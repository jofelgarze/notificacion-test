package com.pruebalib.notification.provider.push;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class PushNotificationSender extends AbstractNotificationSender {

    private static final String TARGET = "push";

    private final PushConfig config;
    private final PushRequestMapper mapper;
    private final PushClient client;

    public PushNotificationSender(PushConfig config) {
        this(config, new PushRequestMapper(), new PushClient());
    }

    public PushNotificationSender(PushConfig config, PushRequestMapper mapper, PushClient client) {
        this.config = Objects.requireNonNull(config, "PushConfig no debe ser nulo");
        this.mapper = Objects.requireNonNull(mapper, "PushRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "PushClient no debe ser nulo");
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
            PushPayload payload = mapper.map(request);
            PushSendResponse response = client.send(payload, config);

            if (response.isAccepted()) {
                return NotificationResult.success(
                        response.getProviderMessageId(),
                        "Push enviado con estado " + response.getStatus());
            }

            String errorMessage = response.getErrorMessage() == null ? "Sin detalle" : response.getErrorMessage();
            return NotificationResult.failure(
                    "Error al enviar Push. status=" + response.getStatus()
                            + ", errorMessage=" + errorMessage);
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure("Error en configuracion Push: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure("Error al enviar Push: " + e.getMessage());
        }
    }
}
