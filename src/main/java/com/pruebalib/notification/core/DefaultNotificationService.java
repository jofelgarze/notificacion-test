package com.pruebalib.notification.core;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.common.exception.NotificationDeliveryException;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.exception.UnsupportedChannelException;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

class DefaultNotificationService implements NotificationService {

    private final NotificationSenderRegistry registry;
    private final Executor executor;

    public DefaultNotificationService(NotificationSenderRegistry registry, Executor executor) {
        this.registry = Objects.requireNonNull(registry, "NotificationSenderRegistry no debe ser nulo");
        this.executor = Objects.requireNonNull(executor, "excecutor no debe ser nulo");
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        if (request == null) {
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.VALIDATION_ERROR,
                    null,
                    null,
                    "REQUEST_NULL",
                    "La notificacion es invalida",
                    "request no debe ser nulo");
        }

        try {
            NotificationSender sender = registry.resolve(request);
            return sender.send(request);
        } catch (NotificationValidationException e) {
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.VALIDATION_ERROR,
                    request.getChannel(),
                    null,
                    "VALIDATION_ERROR",
                    "La notificacion no supera la validacion",
                    e.getMessage());
        } catch (NotificationConfigurationException e) {
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.CONFIGURATION_ERROR,
                    request.getChannel(),
                    null,
                    "CONFIGURATION_ERROR",
                    "La configuracion del proveedor es invalida",
                    e.getMessage());
        } catch (UnsupportedChannelException e) {
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.UNSUPPORTED_CHANNEL,
                    request.getChannel(),
                    null,
                    "UNSUPPORTED_CHANNEL",
                    "No existe un sender compatible",
                    e.getMessage());
        } catch (NotificationDeliveryException e) {
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR,
                    request.getChannel(),
                    null,
                    "DELIVERY_ERROR",
                    "Fallo el envio de la notificacion",
                    e.getMessage());
        } catch (RuntimeException e) {
            return NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR,
                    request.getChannel(),
                    null,
                    "UNEXPECTED_ERROR",
                    "Se produjo un error inesperado durante el envio",
                    e.getMessage());
        }
    }

    @Override
    public CompletableFuture<NotificationResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }
}
