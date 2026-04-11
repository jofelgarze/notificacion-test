package com.pruebalib.notification.spi;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;

public interface NotificationSender {
    boolean supports(NotificationRequest request);

    NotificationResult send(NotificationRequest request);
}