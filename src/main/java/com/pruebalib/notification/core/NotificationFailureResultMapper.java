package com.pruebalib.notification.core;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;

final class NotificationFailureResultMapper {

    NotificationResult requestNull() {
        return NotificationResult.failure(
                NotificationResultType.VALIDATION_ERROR,
                null,
                null,
                "REQUEST_NULL",
                "La notificacion es invalida",
                "request no debe ser nulo");
    }

    NotificationResult validationError(NotificationRequest request, String technicalMessage) {
        return NotificationResult.failure(
                NotificationResultType.VALIDATION_ERROR,
                request.getChannel(),
                null,
                "VALIDATION_ERROR",
                "La notificacion no supera la validacion",
                technicalMessage);
    }

    NotificationResult configurationError(NotificationRequest request, String technicalMessage) {
        return NotificationResult.failure(
                NotificationResultType.CONFIGURATION_ERROR,
                request.getChannel(),
                null,
                "CONFIGURATION_ERROR",
                "La configuracion del proveedor es invalida",
                technicalMessage);
    }

    NotificationResult unsupportedChannel(NotificationRequest request, String technicalMessage) {
        return NotificationResult.failure(
                NotificationResultType.UNSUPPORTED_CHANNEL,
                request.getChannel(),
                null,
                "UNSUPPORTED_CHANNEL",
                "No existe un sender compatible",
                technicalMessage);
    }

    NotificationResult deliveryError(NotificationRequest request, String technicalMessage) {
        return NotificationResult.failure(
                NotificationResultType.DELIVERY_ERROR,
                request.getChannel(),
                null,
                "DELIVERY_ERROR",
                "Fallo el envio de la notificacion",
                technicalMessage);
    }

    NotificationResult unexpectedError(NotificationRequest request, String technicalMessage) {
        return NotificationResult.failure(
                NotificationResultType.DELIVERY_ERROR,
                request.getChannel(),
                null,
                "UNEXPECTED_ERROR",
                "Se produjo un error inesperado durante el envio",
                technicalMessage);
    }
}
