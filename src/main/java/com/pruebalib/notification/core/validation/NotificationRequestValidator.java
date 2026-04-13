package com.pruebalib.notification.core.validation;

import com.pruebalib.notification.api.NotificationRequest;

public interface NotificationRequestValidator {
    void validate(NotificationRequest request);
}
