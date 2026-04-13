package com.pruebalib.notification.provider.push;

import com.pruebalib.notification.spi.NotificationSenderConfig;

public final class PushConfig implements NotificationSenderConfig {

    private final String projectId;
    private final String apiToken;
    private final String baseUrl;

    public PushConfig(String projectId, String apiToken, String baseUrl) {
        this.projectId = requireText(projectId, "El projectId de Push no puede estar vacio");
        this.apiToken = requireText(apiToken, "El apiToken de Push no puede estar vacio");
        this.baseUrl = baseUrl == null || baseUrl.isBlank()
                ? "https://api.push-provider.local"
                : baseUrl;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public String projectId() {
        return projectId;
    }

    public String apiToken() {
        return apiToken;
    }

    public String baseUrl() {
        return baseUrl;
    }
}
