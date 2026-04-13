package com.pruebalib.notification.core;

import java.util.List;
import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;
import com.pruebalib.notification.common.exception.UnsupportedChannelException;
import com.pruebalib.notification.spi.NotificationSender;

final class NotificationDispatchExecutor {

    private final NotificationEventPublisher eventPublisher;

    NotificationDispatchExecutor(NotificationEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher no debe ser nulo");
    }

    NotificationResult execute(NotificationRequest request, List<NotificationSender> candidates) {
        Objects.requireNonNull(request, "request no debe ser nulo");
        Objects.requireNonNull(candidates, "candidates no debe ser nulo");

        if (candidates.isEmpty()) {
            throw new UnsupportedChannelException(
                    "No se encontro sender compatible con channel: " + request.getChannel());
        }

        NotificationResult lastResult = null;
        for (NotificationSender sender : candidates) {
            NotificationResult result = executeWithSender(request, sender);
            if (result.isSuccessful()) {
                return result;
            }

            lastResult = result;
            if (result.getType() != NotificationResultType.DELIVERY_ERROR) {
                return result;
            }
        }

        return lastResult;
    }

    private NotificationResult executeWithSender(NotificationRequest request, NotificationSender sender) {
        eventPublisher.sendStarted(request, sender.provider());

        NotificationResult result = sender.send(request);
        if (result.isSuccessful()) {
            eventPublisher.sendSucceeded(request, result, sender.provider());
            return result;
        }

        eventPublisher.sendFailed(request, result, sender.provider());
        if (result.getType() == NotificationResultType.VALIDATION_ERROR) {
            eventPublisher.validationFailed(request, result, sender.provider());
        }
        return result;
    }
}
