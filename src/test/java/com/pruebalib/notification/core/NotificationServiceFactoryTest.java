package com.pruebalib.notification.core;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.provider.sms.SmsConfig;
import com.pruebalib.notification.provider.sms.SmsNotificationSender;

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
}
