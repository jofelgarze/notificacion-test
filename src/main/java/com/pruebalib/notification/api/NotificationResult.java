package com.pruebalib.notification.api;

public final class NotificationResult {

    private final boolean successful;
    private final String providerMessageId;
    private final String description;

    public NotificationResult(boolean successful, String providerMessageId, String description) {
        this.successful = successful;
        this.providerMessageId = providerMessageId;
        this.description = description;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationResult success(String providerMessageId, String description) {
        return new NotificationResult(true, providerMessageId, description);
    }

    public static NotificationResult failure(String description) {
        return new NotificationResult(false, null, description);
    }
}