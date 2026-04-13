package com.pruebalib.notification.core.validation;

import com.pruebalib.notification.api.NotificationRequest;

public interface ChannelNotificationValidator {
    void validate(NotificationRequest request);
}
