package com.pruebalib.notification.core;

import java.util.List;
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
    private final List<NotificationListener> listeners;
    private final NotificationRoutingPolicy routingPolicy;

    public DefaultNotificationService(NotificationSenderRegistry registry, Executor executor) {
        this(registry, executor, List.of(), new DefaultNotificationRoutingPolicy());
    }

    public DefaultNotificationService(
            NotificationSenderRegistry registry,
            Executor executor,
            List<NotificationListener> listeners) {
        this(registry, executor, listeners, new DefaultNotificationRoutingPolicy());
    }

    public DefaultNotificationService(
            NotificationSenderRegistry registry,
            Executor executor,
            List<NotificationListener> listeners,
            NotificationRoutingPolicy routingPolicy) {
        this.registry = Objects.requireNonNull(registry, "NotificationSenderRegistry no debe ser nulo");
        this.executor = Objects.requireNonNull(executor, "excecutor no debe ser nulo");
        this.listeners = listeners == null ? List.of() : List.copyOf(listeners);
        this.routingPolicy = Objects.requireNonNull(routingPolicy, "routingPolicy no debe ser nulo");
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        if (request == null) {
            NotificationResult result = NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.VALIDATION_ERROR,
                    null,
                    null,
                    "REQUEST_NULL",
                    "La notificacion es invalida",
                    "request no debe ser nulo");
            publish(NotificationEventType.VALIDATION_FAILED, null, result, null, result.getDescription());
            return result;
        }

        try {
            List<NotificationSender> candidates = routingPolicy.order(request, registry.resolveAll(request));
            if (candidates.isEmpty()) {
                throw new UnsupportedChannelException(
                        "No se encontro sender compatible con channel: " + request.getChannel());
            }

            NotificationResult lastResult = null;
            for (NotificationSender sender : candidates) {
                publish(NotificationEventType.SEND_STARTED, request, null, sender.provider(),
                        "Iniciando envio con provider " + sender.provider());
                NotificationResult result = sender.send(request);
                if (result.isSuccessful()) {
                    publish(NotificationEventType.SEND_SUCCEEDED, request, result, sender.provider(),
                            result.getDescription());
                    return result;
                }

                lastResult = result;
                publish(NotificationEventType.SEND_FAILED, request, result, sender.provider(), result.getDescription());
                if (result.getType() != com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR) {
                    if (result.getType() == com.pruebalib.notification.api.NotificationResultType.VALIDATION_ERROR) {
                        publish(NotificationEventType.VALIDATION_FAILED, request, result, sender.provider(),
                                result.getDescription());
                    }
                    return result;
                }
            }

            return lastResult;
        } catch (NotificationValidationException e) {
            NotificationResult result = NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.VALIDATION_ERROR,
                    request.getChannel(),
                    null,
                    "VALIDATION_ERROR",
                    "La notificacion no supera la validacion",
                    e.getMessage());
            publish(NotificationEventType.VALIDATION_FAILED, request, result, null, result.getDescription());
            return result;
        } catch (NotificationConfigurationException e) {
            NotificationResult result = NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.CONFIGURATION_ERROR,
                    request.getChannel(),
                    null,
                    "CONFIGURATION_ERROR",
                    "La configuracion del proveedor es invalida",
                    e.getMessage());
            publish(NotificationEventType.SEND_FAILED, request, result, null, result.getDescription());
            return result;
        } catch (UnsupportedChannelException e) {
            NotificationResult result = NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.UNSUPPORTED_CHANNEL,
                    request.getChannel(),
                    null,
                    "UNSUPPORTED_CHANNEL",
                    "No existe un sender compatible",
                    e.getMessage());
            publish(NotificationEventType.SEND_FAILED, request, result, null, result.getDescription());
            return result;
        } catch (NotificationDeliveryException e) {
            NotificationResult result = NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR,
                    request.getChannel(),
                    null,
                    "DELIVERY_ERROR",
                    "Fallo el envio de la notificacion",
                    e.getMessage());
            publish(NotificationEventType.SEND_FAILED, request, result, null, result.getDescription());
            return result;
        } catch (RuntimeException e) {
            NotificationResult result = NotificationResult.failure(
                    com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR,
                    request.getChannel(),
                    null,
                    "UNEXPECTED_ERROR",
                    "Se produjo un error inesperado durante el envio",
                    e.getMessage());
            publish(NotificationEventType.SEND_FAILED, request, result, null, result.getDescription());
            return result;
        }
    }

    @Override
    public CompletableFuture<NotificationResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }

    private void publish(
            NotificationEventType type,
            NotificationRequest request,
            NotificationResult result,
            String provider,
            String message) {
        NotificationEvent event = new NotificationEvent(type, request, result, provider, message);
        for (NotificationListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
