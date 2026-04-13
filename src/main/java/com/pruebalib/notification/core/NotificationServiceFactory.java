package com.pruebalib.notification.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

public final class NotificationServiceFactory {

    private static final Executor DIRECT_EXECUTOR = Runnable::run;

    private NotificationServiceFactory() {
    }

    public static NotificationService create(List<NotificationSender> senders, Executor executor) {
        List<NotificationSender> validatedSenders = validateSenders(senders);
        Executor validatedExecutor = requireExecutor(executor);

        NotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(validatedSenders);
        return new DefaultNotificationService(registry, validatedExecutor);
    }

    public static NotificationService create(List<NotificationSender> senders) {
        return create(senders, DIRECT_EXECUTOR);
    }

    public static NotificationService create(NotificationSender... senders) {
        if (senders == null) {
            throw new NotificationConfigurationException("senders no debe ser nulo");
        }

        List<NotificationSender> senderList = new ArrayList<>();
        for (NotificationSender sender : senders) {
            senderList.add(requireSender(sender, "ningun sender debe ser nulo"));
        }

        return create(senderList, DIRECT_EXECUTOR);
    }

    private static List<NotificationSender> validateSenders(List<NotificationSender> senders) {
        if (senders == null) {
            throw new NotificationConfigurationException("senders no debe ser nulo");
        }
        if (senders.isEmpty()) {
            throw new NotificationConfigurationException("Debe existir al menos un NotificationSender");
        }

        List<NotificationSender> copy = List.copyOf(senders);
        Set<String> usedChannels = new HashSet<>();
        for (NotificationSender sender : copy) {
            requireSender(sender, "ningun sender debe ser nulo");

            String channel = requireText(sender.channel(), "channel del sender no debe ser nulo");
            if (channel.isEmpty()) {
                throw new NotificationConfigurationException("channel del sender no debe estar vacio");
            }

            String normalizedChannel = channel.toLowerCase();
            if (!usedChannels.add(normalizedChannel)) {
                throw new NotificationConfigurationException(
                        "No se permiten NotificationSender duplicados para el channel: " + channel);
            }
        }

        return copy;
    }

    private static Executor requireExecutor(Executor executor) {
        if (executor == null) {
            throw new NotificationConfigurationException("executor no debe ser nulo");
        }
        return executor;
    }

    private static NotificationSender requireSender(NotificationSender sender, String message) {
        if (sender == null) {
            throw new NotificationConfigurationException(message);
        }
        return sender;
    }

    private static String requireText(String value, String message) {
        if (value == null) {
            throw new NotificationConfigurationException(message);
        }
        return value.trim();
    }
}
