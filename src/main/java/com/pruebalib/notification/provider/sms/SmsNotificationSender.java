package com.pruebalib.notification.provider.sms;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.util.RecipientFormatUtils;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class SmsNotificationSender extends AbstractNotificationSender<SmsConfig> {

    private static final String CHANNEL = "sms";
    private static final String PROVIDER = "sms";

    private final SmsRequestMapper mapper;
    private final SmsClient client;

    public SmsNotificationSender(SmsConfig config) {
        this(config, new SmsRequestMapper(), new SmsClient());
    }

    public SmsNotificationSender(SmsConfig config, SmsRequestMapper mapper, SmsClient client) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "SmsRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "SmsClient no debe ser nulo");
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
            throw new IllegalArgumentException("SmsNotificationSender solo soporta channel sms");
        }
        if (!RecipientFormatUtils.isPhone(request.getRecipient())) {
            throw new IllegalArgumentException("El recipient debe ser un numero telefonico valido para SMS");
        }
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            SmsPayload payload = mapper.map(request, getConfig());
            SmsSendResponse response = client.send(payload, getConfig());

            if (response.isSuccessful()) {
                return NotificationResult.success(
                        response.getProviderMessageId(),
                        "SMS enviado con estado " + response.getStatus()
                                + " y " + response.getSegmentCount() + " segmento(s)");
            }

            String errorCode = response.getErrorCode() == null ? "unknown" : response.getErrorCode();
            String errorMessage = response.getErrorMessage() == null ? "Sin detalle" : response.getErrorMessage();
            return NotificationResult.deliveryError(
                    "Error al enviar SMS. status=" + response.getStatus()
                            + ", errorCode=" + errorCode
                            + ", errorMessage=" + errorMessage);
        } catch (IllegalArgumentException e) {
            return NotificationResult.configurationError("Error en configuracion SMS: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.deliveryError("Error al enviar SMS: " + e.getMessage());
        }
    }
}
