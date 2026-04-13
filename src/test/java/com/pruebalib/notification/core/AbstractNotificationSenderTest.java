package com.pruebalib.notification.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.spi.NotificationSenderConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractNotificationSenderTest {

    @Test
    @DisplayName("supports debe devolver true cuando el channel del request coincide")
    void shouldSupportRequestWhenChannelMatches() {
        TestSender sender = new TestSender("sms");

        boolean result = sender.supports(new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo"));

        assertTrue(result);
    }

    @Test
    @DisplayName("supports debe devolver false cuando el channel del request no coincide")
    void shouldNotSupportRequestWhenChannelDoesNotMatch() {
        TestSender sender = new TestSender("sms");

        boolean result = sender.supports(new NotificationRequest(
                "push",
                "device-token-123",
                "Titulo",
                "Mensaje"));

        assertFalse(result);
    }

    @Test
    @DisplayName("supports debe devolver false cuando el request es nulo")
    void shouldNotSupportNullRequest() {
        TestSender sender = new TestSender("sms");

        assertFalse(sender.supports(null));
    }

    private static final class TestSender extends AbstractNotificationSender<TestConfig> {
        private final String channel;

        private TestSender(String channel) {
            super(new TestConfig());
            this.channel = channel;
        }

        @Override
        public String channel() {
            return channel;
        }

        @Override
        public String provider() {
            return "test";
        }

        @Override
        protected void validateRequest(NotificationRequest request) {
        }

        @Override
        protected NotificationResult doSend(NotificationRequest request) {
            return NotificationResult.success(channel(), provider(), "test-123", "ok");
        }
    }

    private static final class TestConfig implements NotificationSenderConfig {
    }
}
