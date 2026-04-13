package com.pruebalib.notification.provider.gmail;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.util.RecipientFormatUtils;
import com.pruebalib.notification.core.AbstractNotificationSender;

public final class GmailNotificationSender extends AbstractNotificationSender<GmailConfig> {

    private static final String CHANNEL = "email";
    private static final String PROVIDER = "gmail";

    private final GmailRequestMapper mapper;
    private final GmailClient client;

    public GmailNotificationSender(GmailConfig config) {
        this(config, new GmailRequestMapper(), new GmailClient());
    }

    public GmailNotificationSender(GmailConfig config, GmailRequestMapper mapper, GmailClient client) {
        super(config);
        this.mapper = Objects.requireNonNull(mapper, "GmailRequestMapper no debe ser nulo");
        this.client = Objects.requireNonNull(client, "GmailClient no debe ser nulo");
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
            throw new NotificationValidationException("GmailNotificationSender solo soporta channel email");
        }
        if (!RecipientFormatUtils.isEmail(request.getRecipient())) {
            throw new NotificationValidationException("El recipient debe ser un email valido para Gmail");
        }
    }

    @Override
    protected NotificationResult doSend(NotificationRequest request) {
        try {
            GmailPayload payload = mapper.map(request);
            GmailAuthenticator authenticator = GmailAuthenticator.from(getConfig());
            String providerMessageId = client.send(payload, authenticator, getConfig());
            return NotificationResult.success(channel(), provider(), providerMessageId, "Correo enviado con Gmail");
        } catch (IllegalArgumentException e) {
            throw new NotificationConfigurationException("Error en configuracion de Gmail", e);
        } catch (RuntimeException e) {
            throw new NotificationDeliveryException("Error al enviar correo Gmail", e);
        }
    }
}
