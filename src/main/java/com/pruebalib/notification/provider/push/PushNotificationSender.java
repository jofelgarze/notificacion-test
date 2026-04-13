package com.pruebalib.notification.provider.push;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.core.AbstractNotificationSender;
import com.pruebalib.notification.core.validation.ChannelNotificationValidator;
import com.pruebalib.notification.core.validation.DefaultNotificationRequestValidator;
import com.pruebalib.notification.core.validation.PushNotificationValidator;

public final class PushNotificationSender extends AbstractNotificationSender<PushConfig> {

    private static final String CHANNEL = "push";
    private static final String PROVIDER = "push";

    private final PushRequestMapper mapper;
    private final PushClient client;
    private final ChannelNotificationValidator validator;

    public PushNotificationSender(PushConfig config) {
        this(
                config,
                new PushRequestMapper(),
                new PushClient(),
                new PushNotificationValidator(CHANNEL, "Push", new DefaultNotificationRequestValidator()));
    }

    public PushNotificationSender(PushConfig config, PushRequestMapper mapper, PushClient client) {
        this(
                config,
                mapper,
                client,
                new PushNotificationValidator(CHANNEL, "Push", new DefaultNotificationRequestValidator()));
    }

    PushNotificationSender(
            PushConfig config,
            PushRequestMapper mapper,
            PushClient client,
            ChannelNotificationValidator validator) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "PushRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "PushClient no debe ser nulo");
        this.validator = Objects.requireNonNull(validator, "validator no debe ser nulo");
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
        validator.validate(request);
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
