package com.pruebalib.notification.provider.sms;

import java.time.Instant;

final class SmsSendResponse {

    private final String providerMessageId;
    private final String status;
    private final String errorCode;
    private final String errorMessage;
    private final Instant submittedAt;
    private final int segmentCount;

    public SmsSendResponse(
            String providerMessageId,
            String status,
            String errorCode,
            String errorMessage,
            Instant submittedAt,
            int segmentCount) {
        this.providerMessageId = providerMessageId;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.submittedAt = submittedAt;
        this.segmentCount = segmentCount;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    public boolean isSuccessful() {
        return errorCode == null && errorMessage == null && providerMessageId != null && !providerMessageId.isBlank();
    }
}
