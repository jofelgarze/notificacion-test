package com.pruebalib.notification.core;

@FunctionalInterface
public interface NotificationListener {
    void onEvent(NotificationEvent event);
}
