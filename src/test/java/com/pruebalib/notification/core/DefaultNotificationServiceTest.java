package com.pruebalib.notification.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultNotificationServiceTest {

    @Test
    void shouldUseResolvedSenderOnSend() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo");
        NotificationResult expected = NotificationResult.success("SM123", "queued");
        CapturingSender sender = new CapturingSender(expected);
        CapturingRegistry registry = new CapturingRegistry(sender);
        DefaultNotificationService service = new DefaultNotificationService(registry, Runnable::run);

        NotificationResult result = service.send(request);

        assertSame(expected, result);
        assertSame(request, registry.capturedRequest);
        assertSame(request, sender.capturedRequest);
    }

    @Test
    void shouldDelegateAsyncUsingExecutor() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo async");
        NotificationResult expected = NotificationResult.success("SM999", "accepted");
        CapturingSender sender = new CapturingSender(expected);
        CapturingRegistry registry = new CapturingRegistry(sender);
        CountingExecutor executor = new CountingExecutor();
        DefaultNotificationService service = new DefaultNotificationService(registry, executor);

        CompletableFuture<NotificationResult> future = service.sendAsync(request);
        NotificationResult result = future.join();

        assertEquals(1, executor.invocationCount);
        assertTrue(future.isDone());
        assertSame(expected, result);
        assertSame(request, registry.capturedRequest);
        assertSame(request, sender.capturedRequest);
    }

    private static final class CapturingRegistry implements NotificationSenderRegistry {
        private final NotificationSender sender;
        private NotificationRequest capturedRequest;

        private CapturingRegistry(NotificationSender sender) {
            this.sender = sender;
        }

        @Override
        public NotificationSender resolve(NotificationRequest request) {
            this.capturedRequest = request;
            return sender;
        }
    }

    private static final class CapturingSender implements NotificationSender {
        private final NotificationResult result;
        private NotificationRequest capturedRequest;

        private CapturingSender(NotificationResult result) {
            this.result = result;
        }

        @Override
        public boolean supports(NotificationRequest request) {
            return true;
        }

        @Override
        public NotificationResult send(NotificationRequest request) {
            this.capturedRequest = request;
            return result;
        }
    }

    private static final class CountingExecutor implements Executor {
        private int invocationCount;

        @Override
        public void execute(Runnable command) {
            invocationCount++;
            command.run();
        }
    }
}
