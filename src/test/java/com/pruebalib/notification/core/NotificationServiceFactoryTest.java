package com.pruebalib.notification.core;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.provider.gmail.GmailConfig;
import com.pruebalib.notification.provider.gmail.GmailNotificationSender;
import com.pruebalib.notification.provider.sms.SmsConfig;
import com.pruebalib.notification.provider.sms.SmsNotificationSender;
import com.pruebalib.notification.provider.smtp.SmtpConfig;
import com.pruebalib.notification.provider.smtp.SmtpNotificationSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceFactoryTest {

    @Test
    void shouldCreateServiceWithDefaultRegistryAndExecutor() {
        NotificationService service = NotificationServiceFactory.create(List.of(
                new SmsNotificationSender(
                        new SmsConfig("acct-123", "token-xyz", "+15005550006", "https://api.sms-provider.local"))));

        NotificationResult result = service.send(new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo"));

        assertTrue(result.isSuccessful());
    }

    @Test
    void shouldCreateServiceUsingSenderVarargs() {
        assertDoesNotThrow(() -> NotificationServiceFactory.create(
                new SmsNotificationSender(
                        new SmsConfig("acct-123", "token-xyz", "+15005550006", "https://api.sms-provider.local"))));
    }

    @Test
    void shouldRejectEmptySenderList() {
        assertThrows(NotificationConfigurationException.class,
                () -> NotificationServiceFactory.create(List.of()));
    }

    @Test
    void shouldAllowDifferentProvidersForSameChannel() {
        assertDoesNotThrow(() -> NotificationServiceFactory.create(List.of(
                new GmailNotificationSender(
                        new GmailConfig("user1@gmail.com", "secret", null, null, 587, true, false)),
                new SmtpNotificationSender(
                        new SmtpConfig("user2@example.com", "secret", null, "smtp.example.com", 587, true, false)))));
    }

    @Test
    void shouldRejectDuplicateChannelAndProvider() {
        assertThrows(NotificationConfigurationException.class, () -> NotificationServiceFactory.create(List.of(
                new SmtpNotificationSender(
                        new SmtpConfig("user1@example.com", "secret", null, "smtp.example.com", 587, true, false)),
                new SmtpNotificationSender(
                        new SmtpConfig("user2@example.com", "secret", null, "smtp2.example.com", 587, true, false)))));
    }
}
