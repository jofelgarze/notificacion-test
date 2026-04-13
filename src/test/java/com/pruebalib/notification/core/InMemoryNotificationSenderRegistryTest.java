package com.pruebalib.notification.core;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.common.exception.UnsupportedChannelException;
import com.pruebalib.notification.provider.gmail.GmailConfig;
import com.pruebalib.notification.provider.gmail.GmailNotificationSender;
import com.pruebalib.notification.provider.push.PushConfig;
import com.pruebalib.notification.provider.push.PushNotificationSender;
import com.pruebalib.notification.provider.sms.SmsConfig;
import com.pruebalib.notification.provider.sms.SmsNotificationSender;
import com.pruebalib.notification.provider.smtp.SmtpConfig;
import com.pruebalib.notification.provider.smtp.SmtpNotificationSender;
import com.pruebalib.notification.spi.NotificationSender;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryNotificationSenderRegistryTest {

        @Test
        void shouldResolveSmsSender() {
                SmsNotificationSender smsSender = new SmsNotificationSender(
                                new SmsConfig("acct-123", "token-xyz", "+15005550006",
                                                "https://api.sms-provider.local"));
                PushNotificationSender pushSender = new PushNotificationSender(
                                new PushConfig("project-demo", "token-demo", "https://api.push-provider.local"));

                InMemoryNotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                                List.of(pushSender, smsSender));

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
                                new SmsConfig("acct-123", "token-xyz", "+15005550006",
                                                "https://api.sms-provider.local"));
                NotificationSender pushSender = new PushNotificationSender(
                                new PushConfig("project-demo", "token-demo", "https://api.push-provider.local"));

                InMemoryNotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                                List.of(smsSender, pushSender));

                NotificationSender resolved = registry.resolve(new NotificationRequest(
                                "push",
                                "device-token",
                                "Titulo",
                                "Mensaje"));

                assertInstanceOf(PushNotificationSender.class, resolved);
                assertSame(pushSender, resolved);
        }

        @Test
        void shouldResolveFirstConfiguredEmailProviderUsingUnifiedChannel() {
                NotificationSender gmailSender = new GmailNotificationSender(
                                new GmailConfig("user@gmail.com", "secret", null, null, 587, true, false));
                NotificationSender smtpSender = new SmtpNotificationSender(
                                new SmtpConfig("user@example.com", "secret", null, "smtp.example.com", 587, true,
                                                false));

                InMemoryNotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                                List.of(gmailSender, smtpSender));

                NotificationSender resolved = registry.resolve(new NotificationRequest(
                                "email",
                                "dest@example.com",
                                "Asunto",
                                "Mensaje"));

                assertSame(gmailSender, resolved);
        }

        @Test
        void shouldThrowWhenNoCompatibleSenderExists() {
                InMemoryNotificationSenderRegistry registry = new InMemoryNotificationSenderRegistry(
                                List.of(new PushNotificationSender(
                                                new PushConfig("project-demo", "token-demo",
                                                                "https://api.push-provider.local"))));

                assertThrows(UnsupportedChannelException.class, () -> registry.resolve(new NotificationRequest(
                                "sms",
                                "+593999999999",
                                null,
                                "Codigo")));
        }
}
