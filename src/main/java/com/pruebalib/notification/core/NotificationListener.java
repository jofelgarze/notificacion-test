package com.pruebalib.notification.core;

@FunctionalInterface
interface NotificationListener {
    void onEvent(NotificationEvent event);
}
