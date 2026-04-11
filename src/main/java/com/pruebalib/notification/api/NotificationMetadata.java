package com.pruebalib.notification.api;

import java.util.Map;

public final class NotificationMetadata {

    private final Map<String, Object> values;

    public NotificationMetadata(Map<String, Object> values) {
        this.values = values == null ? Map.of() : Map.copyOf(values);
    }

    public String getString(String key) {
        Object value = values.get(key);
        return value instanceof String str ? str : null;
    }

    public Object get(String key) {
        return values.get(key);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public Map<String, Object> asMap() {
        return values;
    }
}