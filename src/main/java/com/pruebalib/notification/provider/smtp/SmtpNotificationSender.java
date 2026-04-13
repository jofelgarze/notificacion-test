package com.pruebalib.notification.provider.smtp;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.core.AbstractNotificationSender;
import com.pruebalib.notification.core.validation.ChannelNotificationValidator;
import com.pruebalib.notification.core.validation.DefaultNotificationRequestValidator;
import com.pruebalib.notification.core.validation.EmailNotificationValidator;

public final class SmtpNotificationSender extends AbstractNotificationSender<SmtpConfig> {

    private static final String CHANNEL = "email";
    private static final String PROVIDER = "smtp";

    private final SmtpRequestMapper mapper;
    private final SmtpClient client;
    private final ChannelNotificationValidator validator;

    public SmtpNotificationSender(SmtpConfig config) {
        this(
                config,
                new SmtpRequestMapper(),
                new SmtpClient(),
                new EmailNotificationValidator(CHANNEL, "SMTP", new DefaultNotificationRequestValidator()));
    }

    public SmtpNotificationSender(SmtpConfig config, SmtpRequestMapper mapper, SmtpClient client) {
        this(
                config,
                mapper,
                client,
                new EmailNotificationValidator(CHANNEL, "SMTP", new DefaultNotificationRequestValidator()));
    }

    SmtpNotificationSender(
            SmtpConfig config,
            SmtpRequestMapper mapper,
            SmtpClient client,
            ChannelNotificationValidator validator) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "SmtpRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "SmtpClient no debe ser nulo");
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
            SmtpPayload payload = mapper.map(request);
            SmtpAuthenticator authenticator = SmtpAuthenticator.from(getConfig());
            String providerMessageId = client.send(payload, authenticator, getConfig());
            return NotificationResult.success(channel(), provider(), providerMessageId, "Correo enviado via SMTP");
        } catch (RuntimeException e) {
            throw new NotificationDeliveryException("Error al enviar correo SMTP", e);
        }
    }
}
