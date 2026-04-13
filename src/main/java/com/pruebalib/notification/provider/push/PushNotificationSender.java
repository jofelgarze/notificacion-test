package com.pruebalib.notification.provider.push;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.util.RecipientFormatUtils;
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
        requireRequest(request);
        requireChannel(request);
        requireRecipient(request);
        requireMessage(request);
        if (!CHANNEL.equalsIgnoreCase(request.getChannel())) {
            throw new NotificationValidationException("PushNotificationSender solo soporta channel push");
        }
        if (!RecipientFormatUtils.isPushToken(request.getRecipient())) {
            throw new NotificationValidationException("El recipient debe ser un token valido para Push");
        }
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            PushPayload payload = mapper.map(request);
            PushSendResponse response = client.send(payload, getConfig());

            if (response.isAccepted()) {
                return NotificationResult.success(
                        channel(),
                        provider(),
                        response.getProviderMessageId(),
                        "Push enviado con estado " + response.getStatus());
            }

            String errorMessage = response.getErrorMessage() == null ? "Sin detalle" : response.getErrorMessage();
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR,
                    channel(),
                    provider(),
                    "PUSH_REJECTED",
                    "Error al enviar Push. status=" + response.getStatus()
                            + ", errorMessage=" + errorMessage,
                    response.getErrorMessage());
        } catch (RuntimeException e) {
            throw new NotificationDeliveryException("Error al enviar Push", e);
        }
    }
}
