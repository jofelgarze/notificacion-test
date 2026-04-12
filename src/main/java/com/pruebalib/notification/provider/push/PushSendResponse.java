package com.pruebalib.notification.provider.push;

public final class PushSendResponse {

    private final String providerMessageId;
    private final String status;
    private final boolean accepted;
    private final String errorMessage;

    public PushSendResponse(String providerMessageId, String status, boolean accepted, String errorMessage) {
        this.providerMessageId = providerMessageId;
        this.status = status;
        this.accepted = accepted;
        this.errorMessage = errorMessage;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
