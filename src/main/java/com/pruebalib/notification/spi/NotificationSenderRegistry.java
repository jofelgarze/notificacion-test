package com.pruebalib.notification.spi;

import java.util.List;

import com.pruebalib.notification.api.NotificationRequest;

public interface NotificationSenderRegistry {
    NotificationSender resolve(NotificationRequest request);

    List<NotificationSender> resolveAll(NotificationRequest request);
}
