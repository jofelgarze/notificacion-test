package com.pruebalib.notification.provider.sms;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.core.AbstractNotificationSender;
import com.pruebalib.notification.core.validation.ChannelNotificationValidator;
import com.pruebalib.notification.core.validation.DefaultNotificationRequestValidator;
import com.pruebalib.notification.core.validation.SmsNotificationValidator;

public final class SmsNotificationSender extends AbstractNotificationSender<SmsConfig> {

    private static final String CHANNEL = "sms";
    private static final String PROVIDER = "sms";

    private final SmsRequestMapper mapper;
    private final SmsClient client;
    private final ChannelNotificationValidator validator;

    public SmsNotificationSender(SmsConfig config) {
        this(
                config,
                new SmsRequestMapper(),
                new SmsClient(),
                new SmsNotificationValidator(CHANNEL, "SMS", new DefaultNotificationRequestValidator()));
    }

    public SmsNotificationSender(SmsConfig config, SmsRequestMapper mapper, SmsClient client) {
        this(
                config,
                mapper,
                client,
                new SmsNotificationValidator(CHANNEL, "SMS", new DefaultNotificationRequestValidator()));
    }

    SmsNotificationSender(
            SmsConfig config,
            SmsRequestMapper mapper,
            SmsClient client,
            ChannelNotificationValidator validator) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "SmsRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "SmsClient no debe ser nulo");
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
            SmsPayload payload = mapper.map(request, getConfig());
            SmsSendResponse response = client.send(payload, getConfig());

            if (response.isSuccessful()) {
                return NotificationResult.success(
                        channel(),
                        provider(),
                        response.getProviderMessageId(),
                        "SMS enviado con estado " + response.getStatus()
                                + " y " + response.getSegmentCount() + " segmento(s)");
            }

            String errorCode = response.getErrorCode() == null ? "unknown" : response.getErrorCode();
            String errorMessage = response.getErrorMessage() == null ? "Sin detalle" : response.getErrorMessage();
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR,
                    channel(),
                    provider(),
                    errorCode,
                    "Error al enviar SMS. status=" + response.getStatus()
                            + ", errorMessage=" + errorMessage,
                    response.getErrorMessage());
        } catch (RuntimeException e) {
            throw new NotificationDeliveryException("Error al enviar SMS", e);
        }
    }
}
