package com.pruebalib.notification.provider.gmail;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.core.AbstractNotificationSender;
import com.pruebalib.notification.core.validation.ChannelNotificationValidator;
import com.pruebalib.notification.core.validation.DefaultNotificationRequestValidator;
import com.pruebalib.notification.core.validation.EmailNotificationValidator;

public final class GmailNotificationSender extends AbstractNotificationSender<GmailConfig> {

    private static final String CHANNEL = "email";
    private static final String PROVIDER = "gmail";

    private final GmailRequestMapper mapper;
    private final GmailClient client;
    private final ChannelNotificationValidator validator;

    public GmailNotificationSender(GmailConfig config) {
        this(
                config,
                new GmailRequestMapper(),
                new GmailClient(),
                new EmailNotificationValidator(CHANNEL, "Gmail", new DefaultNotificationRequestValidator()));
    }

    public GmailNotificationSender(GmailConfig config, GmailRequestMapper mapper, GmailClient client) {
        this(
                config,
                mapper,
                client,
                new EmailNotificationValidator(CHANNEL, "Gmail", new DefaultNotificationRequestValidator()));
    }

    GmailNotificationSender(
            GmailConfig config,
            GmailRequestMapper mapper,
            GmailClient client,
            ChannelNotificationValidator validator) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "GmailRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "GmailClient no debe ser nulo");
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
            GmailPayload payload = mapper.map(request);
            GmailAuthenticator authenticator = GmailAuthenticator.from(getConfig());
            String providerMessageId = client.send(payload, authenticator, getConfig());
            return NotificationResult.success(channel(), provider(), providerMessageId, "Correo enviado con Gmail");
        } catch (RuntimeException e) {
            throw new NotificationDeliveryException("Error al enviar correo Gmail", e);
        }
    }
}
