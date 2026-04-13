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
    private final NotificationDispatchExecutor dispatchExecutor;

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
        this.dispatchExecutor = new NotificationDispatchExecutor(eventPublisher);
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        if (request == null) {
            NotificationResult result = resultMapper.requestNull();
            eventPublisher.validationFailed(null, result, null);
            return result;
        }

        try {
            List<NotificationSender> candidates = routingPolicy.order(request, registry.resolveAll(request));
            return dispatchExecutor.execute(request, candidates);
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
}
