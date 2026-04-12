package com.pruebalib.notification.provider.sms;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class SmsNotificationSender extends AbstractNotificationSender {

    private static final String TARGET = "sms";

    private final SmsConfig config;
    private final SmsRequestMapper mapper;
    private final SmsClient client;

    public SmsNotificationSender(SmsConfig config) {
        this(config, new SmsRequestMapper(), new SmsClient());
    }

    public SmsNotificationSender(SmsConfig config, SmsRequestMapper mapper, SmsClient client) {
        this.config = Objects.requireNonNull(config, "SmsConfig no debe ser nulo");
        this.mapper = Objects.requireNonNull(mapper, "SmsRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "SmsClient no debe ser nulo");
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
            SmsPayload payload = mapper.map(request, config);
            SmsSendResponse response = client.send(payload, config);

            if (response.isSuccessful()) {
                return NotificationResult.success(
                        response.getProviderMessageId(),
                        "SMS enviado con estado " + response.getStatus()
                                + " y " + response.getSegmentCount() + " segmento(s)");
            }

            String errorCode = response.getErrorCode() == null ? "unknown" : response.getErrorCode();
            String errorMessage = response.getErrorMessage() == null ? "Sin detalle" : response.getErrorMessage();
            return NotificationResult.failure(
                    "Error al enviar SMS. status=" + response.getStatus()
                            + ", errorCode=" + errorCode
                            + ", errorMessage=" + errorMessage);
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure("Error en configuracion SMS: " + e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure("Error al enviar SMS: " + e.getMessage());
        }
    }
}
