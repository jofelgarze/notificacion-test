package com.pruebalib.notification.api;

public final class NotificationResult {

    private final NotificationResultType type;
    private final boolean successful;
    private final String providerMessageId;
    private final String description;

    public NotificationResult(boolean successful, String providerMessageId, String description) {
        this(successful ? NotificationResultType.SUCCESS : NotificationResultType.DELIVERY_ERROR,
                successful,
                providerMessageId,
                description);
    }

    public NotificationResult(
            NotificationResultType type,
            boolean successful,
            String providerMessageId,
            String description) {
        this.type = type;
        this.successful = successful;
        this.providerMessageId = providerMessageId;
        this.description = description;
    }

    public NotificationResultType getType() {
        return type;
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
        return new NotificationResult(NotificationResultType.SUCCESS, true, providerMessageId, description);
    }

    public static NotificationResult validationError(String description) {
        return new NotificationResult(NotificationResultType.VALIDATION_ERROR, false, null, description);
    }

    public static NotificationResult configurationError(String description) {
        return new NotificationResult(NotificationResultType.CONFIGURATION_ERROR, false, null, description);
    }

    public static NotificationResult deliveryError(String description) {
        return new NotificationResult(NotificationResultType.DELIVERY_ERROR, false, null, description);
    }

    public static NotificationResult unsupportedChannel(String description) {
        return new NotificationResult(NotificationResultType.UNSUPPORTED_CHANNEL, false, null, description);
    }

    public static NotificationResult failure(String description) {
        return deliveryError(description);
    }
}
