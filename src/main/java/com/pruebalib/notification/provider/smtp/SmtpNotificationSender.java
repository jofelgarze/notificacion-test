package com.pruebalib.notification.provider.smtp;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.util.RecipientFormatUtils;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class SmtpNotificationSender extends AbstractNotificationSender<SmtpConfig> {

    private static final String CHANNEL = "email";
    private static final String PROVIDER = "smtp";

    private final SmtpRequestMapper mapper;
    private final SmtpClient client;

    public SmtpNotificationSender(SmtpConfig config) {
        this(config, new SmtpRequestMapper(), new SmtpClient());
    }

    public SmtpNotificationSender(SmtpConfig config, SmtpRequestMapper mapper, SmtpClient client) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "SmtpRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "SmtpClient no debe ser nulo");
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
            throw new NotificationValidationException("SmtpNotificationSender solo soporta channel email");
        }
        if (!RecipientFormatUtils.isEmail(request.getRecipient())) {
            throw new NotificationValidationException("El recipient debe ser un email valido para SMTP");
        }
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            SmtpPayload payload = mapper.map(request);
            SmtpAuthenticator authenticator = SmtpAuthenticator.from(getConfig());
            String providerMessageId = client.send(payload, authenticator, getConfig());
            return NotificationResult.success(channel(), provider(), providerMessageId, "Correo enviado via SMTP");
        } catch (RuntimeException e) {
            throw new NotificationDeliveryException("Error al enviar correo SMTP", e);
        }
    }
}
