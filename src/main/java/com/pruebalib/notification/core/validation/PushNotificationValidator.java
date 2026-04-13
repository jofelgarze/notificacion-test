package com.pruebalib.notification.core.validation;

import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.util.RecipientFormatUtils;

public final class PushNotificationValidator implements ChannelNotificationValidator {

    private final String channel;
    private final String providerName;
    private final NotificationRequestValidator requestValidator;

    public PushNotificationValidator(String channel, String providerName, NotificationRequestValidator requestValidator) {
        this.channel = Objects.requireNonNull(channel, "channel no debe ser nulo");
        this.providerName = Objects.requireNonNull(providerName, "providerName no debe ser nulo");
        this.requestValidator = Objects.requireNonNull(requestValidator, "requestValidator no debe ser nulo");
    }

    @Override
    public void validate(NotificationRequest request) {
        requestValidator.validate(request);
        if (!channel.equalsIgnoreCase(request.getChannel())) {
            throw new NotificationValidationException(
                    providerName + " solo soporta channel " + channel);
        }
        if (!RecipientFormatUtils.isPushToken(request.getRecipient())) {
            throw new NotificationValidationException(
                    "El recipient debe ser un token valido para " + providerName);
        }
    }
}
