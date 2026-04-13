package com.pruebalib.notification.common.exception;

public final class NotificationDeliveryException extends NotificationException {

    public NotificationDeliveryException(String message) {
        super(message);
    }

    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
