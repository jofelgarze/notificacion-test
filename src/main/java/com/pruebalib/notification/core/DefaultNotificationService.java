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
    private final NotificationRoutingPolicy routingPolicy;
    private final NotificationEventPublisher eventPublisher;
    private final NotificationFailureResultMapper resultMapper;

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
        this.routingPolicy = Objects.requireNonNull(routingPolicy, "routingPolicy no debe ser nulo");
        this.eventPublisher = new NotificationEventPublisher(listeners);
        this.resultMapper = new NotificationFailureResultMapper();
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        if (request == null) {
            NotificationResult result = resultMapper.requestNull();
            eventPublisher.validationFailed(null, result, null);
            return result;
        }

        try {
            return sendWithResolvedCandidates(request);
        } catch (NotificationValidationException e) {
            NotificationResult result = resultMapper.validationError(request, e.getMessage());
            eventPublisher.validationFailed(request, result, null);
            return result;
        } catch (NotificationConfigurationException e) {
            NotificationResult result = resultMapper.configurationError(request, e.getMessage());
            eventPublisher.sendFailed(request, result, null);
            return result;
        } catch (UnsupportedChannelException e) {
            NotificationResult result = resultMapper.unsupportedChannel(request, e.getMessage());
            eventPublisher.sendFailed(request, result, null);
            return result;
        } catch (NotificationDeliveryException e) {
            NotificationResult result = resultMapper.deliveryError(request, e.getMessage());
            eventPublisher.sendFailed(request, result, null);
            return result;
        } catch (RuntimeException e) {
            NotificationResult result = resultMapper.unexpectedError(request, e.getMessage());
            eventPublisher.sendFailed(request, result, null);
            return result;
        }
    }

    @Override
    public CompletableFuture<NotificationResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }

    private NotificationResult sendWithResolvedCandidates(NotificationRequest request) {
        List<NotificationSender> candidates = routingPolicy.order(request, registry.resolveAll(request));
        if (candidates.isEmpty()) {
            throw new UnsupportedChannelException(
                    "No se encontro sender compatible con channel: " + request.getChannel());
        }

        NotificationResult lastResult = null;
        for (NotificationSender sender : candidates) {
            NotificationResult result = sendWithSender(request, sender);
            if (result.isSuccessful()) {
                return result;
            }

            lastResult = result;
            if (result.getType() != com.pruebalib.notification.api.NotificationResultType.DELIVERY_ERROR) {
                return result;
            }
        }

        return lastResult;
    }

    private NotificationResult sendWithSender(NotificationRequest request, NotificationSender sender) {
        eventPublisher.sendStarted(request, sender.provider());
        NotificationResult result = sender.send(request);
        if (result.isSuccessful()) {
            eventPublisher.sendSucceeded(request, result, sender.provider());
            return result;
        }

        eventPublisher.sendFailed(request, result, sender.provider());
        if (result.getType() == com.pruebalib.notification.api.NotificationResultType.VALIDATION_ERROR) {
            eventPublisher.validationFailed(request, result, sender.provider());
        }
        return result;
    }
}
