package com.pruebalib.notification.core;

import java.util.List;
import java.util.Objects;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.exception.UnsupportedChannelException;
import com.pruebalib.notification.spi.NotificationSender;

final class NotificationDispatchExecutor {

    private final NotificationEventPublisher eventPublisher;
    private final NotificationFailureResultMapper resultMapper;

    NotificationDispatchExecutor(
            NotificationEventPublisher eventPublisher,
            NotificationFailureResultMapper resultMapper) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher no debe ser nulo");
        this.resultMapper = Objects.requireNonNull(resultMapper, "resultMapper no debe ser nulo");
    }

    NotificationResult execute(NotificationRequest request, List<NotificationSender> candidates, String trackerId) {
        Objects.requireNonNull(request, "request no debe ser nulo");
        Objects.requireNonNull(candidates, "candidates no debe ser nulo");

        if (candidates.isEmpty()) {
            throw new UnsupportedChannelException(
                    "No se encontro sender compatible con channel: " + request.getChannel());
        }

        NotificationResult lastResult = null;
        for (NotificationSender sender : candidates) {
            NotificationResult result = executeWithSender(request, sender, trackerId);
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

    private NotificationResult executeWithSender(
            NotificationRequest request,
            NotificationSender sender,
            String trackerId) {
        eventPublisher.sendStarted(request, sender.provider(), trackerId);

        NotificationResult result;
        try {
            result = sender.send(request);
        } catch (NotificationValidationException e) {
            result = resultMapper.validationError(request, sender.provider(), e.getMessage());
            eventPublisher.sendFailed(request, result, sender.provider(), trackerId);
            eventPublisher.validationFailed(request, result, sender.provider(), trackerId);
            return result;
        } catch (NotificationConfigurationException e) {
            result = resultMapper.configurationError(request, sender.provider(), e.getMessage());
            eventPublisher.sendFailed(request, result, sender.provider(), trackerId);
            return result;
        } catch (NotificationDeliveryException e) {
            result = resultMapper.deliveryError(request, sender.provider(), e.getMessage());
            eventPublisher.sendFailed(request, result, sender.provider(), trackerId);
            return result;
        } catch (RuntimeException e) {
            result = resultMapper.unexpectedError(request, sender.provider(), e.getMessage());
            eventPublisher.sendFailed(request, result, sender.provider(), trackerId);
            return result;
        }

        if (result.isSuccessful()) {
            eventPublisher.sendSucceeded(request, result, sender.provider(), trackerId);
            return result;
        }

        eventPublisher.sendFailed(request, result, sender.provider(), trackerId);
        if (result.getType() == NotificationResultType.VALIDATION_ERROR) {
            eventPublisher.validationFailed(request, result, sender.provider(), trackerId);
        }
        return result;
    }
}
