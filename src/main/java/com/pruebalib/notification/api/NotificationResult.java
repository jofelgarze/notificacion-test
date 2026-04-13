package com.pruebalib.notification.api;

public final class NotificationResult {

    private final NotificationResultType type;
    private final boolean successful;
    private final String channel;
    private final String provider;
    private final String providerMessageId;
    private final String errorCode;
    private final String description;
    private final String technicalMessage;

    public NotificationResult(boolean successful, String providerMessageId, String description) {
        this(successful ? NotificationResultType.SUCCESS : NotificationResultType.DELIVERY_ERROR,
                successful,
                null,
                null,
                providerMessageId,
                null,
                description,
                null);
    }

    public NotificationResult(
            NotificationResultType type,
            boolean successful,
            String channel,
            String provider,
            String providerMessageId,
            String errorCode,
            String description,
            String technicalMessage) {
        this.type = type;
        this.successful = successful;
        this.channel = channel;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
        this.errorCode = errorCode;
        this.description = description;
        this.technicalMessage = technicalMessage;
    }

    public NotificationResultType getType() {
        return type;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getChannel() {
        return channel;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }

    public static NotificationResult success(String providerMessageId, String description) {
        return success(null, null, providerMessageId, description);
    }

    public static NotificationResult success(
            String channel,
            String provider,
            String providerMessageId,
            String description) {
        return new NotificationResult(
                NotificationResultType.SUCCESS,
                true,
                channel,
                provider,
                providerMessageId,
                null,
                description,
                null);
    }

    public static NotificationResult validationError(String description) {
        return failure(NotificationResultType.VALIDATION_ERROR, null, null, "VALIDATION_ERROR", description, null);
    }

    public static NotificationResult configurationError(String description) {
        return failure(NotificationResultType.CONFIGURATION_ERROR, null, null, "CONFIGURATION_ERROR", description, null);
    }

    public static NotificationResult deliveryError(String description) {
        return failure(NotificationResultType.DELIVERY_ERROR, null, null, "DELIVERY_ERROR", description, null);
    }

    public static NotificationResult unsupportedChannel(String description) {
        return failure(NotificationResultType.UNSUPPORTED_CHANNEL, null, null, "UNSUPPORTED_CHANNEL", description,
                null);
    }

    public static NotificationResult failure(
            NotificationResultType type,
            String channel,
            String provider,
            String errorCode,
            String description,
            String technicalMessage) {
        return new NotificationResult(type, false, channel, provider, null, errorCode, description, technicalMessage);
    }

    public static NotificationResult failure(String description) {
        return deliveryError(description);
    }
}
