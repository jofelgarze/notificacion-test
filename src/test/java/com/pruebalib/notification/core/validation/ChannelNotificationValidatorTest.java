package com.pruebalib.notification.core.validation;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.common.exception.NotificationValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChannelNotificationValidatorTest {

    @Test
    void shouldValidateEmailRequests() {
        ChannelNotificationValidator validator = new EmailNotificationValidator(
                "email",
                "Gmail",
                new DefaultNotificationRequestValidator());

        assertDoesNotThrow(() -> validator.validate(
                new NotificationRequest("email", "dest@example.com", "Asunto", "Mensaje")));
    }

    @Test
    void shouldRejectInvalidEmailRecipient() {
        ChannelNotificationValidator validator = new EmailNotificationValidator(
                "email",
                "Gmail",
                new DefaultNotificationRequestValidator());

        assertThrows(NotificationValidationException.class,
                () -> validator.validate(new NotificationRequest("email", "no-email", "Asunto", "Mensaje")));
    }

    @Test
    void shouldValidateSmsRequests() {
        ChannelNotificationValidator validator = new SmsNotificationValidator(
                "sms",
                "SMS",
                new DefaultNotificationRequestValidator());

        assertDoesNotThrow(() -> validator.validate(
                new NotificationRequest("sms", "+593999999999", null, "Codigo")));
    }

    @Test
    void shouldRejectInvalidSmsRecipient() {
        ChannelNotificationValidator validator = new SmsNotificationValidator(
                "sms",
                "SMS",
                new DefaultNotificationRequestValidator());

        assertThrows(NotificationValidationException.class,
                () -> validator.validate(new NotificationRequest("sms", "abc", null, "Codigo")));
    }

    @Test
    void shouldValidatePushRequests() {
        ChannelNotificationValidator validator = new PushNotificationValidator(
                "push",
                "Push",
                new DefaultNotificationRequestValidator());

        assertDoesNotThrow(() -> validator.validate(
                new NotificationRequest("push", "abcdef1234567890", null, "Mensaje")));
    }

    @Test
    void shouldRejectInvalidPushRecipient() {
        ChannelNotificationValidator validator = new PushNotificationValidator(
                "push",
                "Push",
                new DefaultNotificationRequestValidator());

        assertThrows(NotificationValidationException.class,
                () -> validator.validate(new NotificationRequest("push", "short", null, "Mensaje")));
    }
}
