package com.pruebalib.notification.provider.sms;

import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.spi.NotificationSenderConfig;

public final class SmsConfig implements NotificationSenderConfig {

    private final String accountId;
    private final String authToken;
    private final String from;
    private final String baseUrl;

    public SmsConfig(String accountId, String authToken, String from, String baseUrl) {
        this.accountId = requireText(accountId, "La cuenta del proveedor SMS no puede estar vacia");
        this.authToken = requireText(authToken, "El token del proveedor SMS no puede estar vacio");
        this.from = requireText(from, "El remitente SMS no puede estar vacio");
        this.baseUrl = baseUrl == null || baseUrl.isBlank()
                ? "https://api.sms-provider.local"
                : baseUrl;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new NotificationConfigurationException(message);
        }
        return value;
    }

    public String accountId() {
        return accountId;
    }

    public String authToken() {
        return authToken;
    }

    public String from() {
        return from;
    }

    public String baseUrl() {
        return baseUrl;
    }
}
