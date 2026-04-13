package com.pruebalib.notification.provider.push;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class PushNotificationSender extends AbstractNotificationSender<PushConfig> {

    private static final String CHANNEL = "push";
    private static final String PROVIDER = "push";

    private final PushRequestMapper mapper;
    private final PushClient client;

    public PushNotificationSender(PushConfig config) {
        this(config, new PushRequestMapper(), new PushClient());
    }

    public PushNotificationSender(PushConfig config, PushRequestMapper mapper, PushClient client) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "PushRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "PushClient no debe ser nulo");
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
            PushPayload payload = mapper.map(request);
            PushSendResponse response = client.send(payload, getConfig());

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
