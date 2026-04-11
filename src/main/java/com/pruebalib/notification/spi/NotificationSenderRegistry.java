package com.pruebalib.notification.spi;

import com.pruebalib.notification.api.NotificationRequest;

public interface NotificationSenderRegistry {
    NotificationSender resolve(NotificationRequest request);
}