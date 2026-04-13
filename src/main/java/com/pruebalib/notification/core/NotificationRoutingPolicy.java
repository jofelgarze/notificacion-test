package com.pruebalib.notification.core;

import java.util.List;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.spi.NotificationSender;

interface NotificationRoutingPolicy {
    List<NotificationSender> order(NotificationRequest request, List<NotificationSender> candidates);
}
