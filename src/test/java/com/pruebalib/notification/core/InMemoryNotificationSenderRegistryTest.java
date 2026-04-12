package com.pruebalib.notification.core;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.provider.sms.SmsConfig;
import com.pruebalib.notification.provider.sms.SmsNotificationSender;
import com.pruebalib.notification.spi.NotificationSender;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryNotificationSenderRegistryTest {

    @Test
    void shouldResolveSmsSender() {
        SmsNotificationSender smsSender = new SmsNotificationSender(
                new SmsConfig("acct-123", "token-xyz", "+15005550006", "https://api.sms-provider.local"));
        NotificationSender pushLikeSender = new FixedTargetSender("push");

        InMemoryNotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                List.of(pushLikeSender, smsSender));

        NotificationSender resolved = registry.resolve(new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo"));

        assertSame(smsSender, resolved);
    }

    @Test
    void shouldResolveOtherCompatibleSender() {
        NotificationSender smsSender = new SmsNotificationSender(
                new SmsConfig("acct-123", "token-xyz", "+15005550006", "https://api.sms-provider.local"));
        NotificationSender pushLikeSender = new FixedTargetSender("push");

        InMemoryNotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                List.of(smsSender, pushLikeSender));

        NotificationSender resolved = registry.resolve(new NotificationRequest(
                "push",
                "device-token",
                "Titulo",
                "Mensaje"));

        assertInstanceOf(FixedTargetSender.class, resolved);
        assertSame(pushLikeSender, resolved);
    }

    @Test
    void shouldThrowWhenNoCompatibleSenderExists() {
        InMemoryNotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                List.of(new FixedTargetSender("push")));

        assertThrows(IllegalArgumentException.class, () -> registry.resolve(new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo")));
    }

    private static final class FixedTargetSender implements NotificationSender {
        private final String target;

        private FixedTargetSender(String target) {
            this.target = target;
        }

        @Override
        public boolean supports(NotificationRequest request) {
            return request != null && target.equalsIgnoreCase(request.getTarget());
        }

        @Override
        public NotificationResult send(NotificationRequest request) {
            return NotificationResult.success("fixed-id", "fixed");
        }
    }
}
