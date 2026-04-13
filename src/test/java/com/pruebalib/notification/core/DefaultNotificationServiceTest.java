package com.pruebalib.notification.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;
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

    @Test
    void shouldUseSameClientFlowForUnifiedEmailChannel() {
        NotificationRequest request = new NotificationRequest(
                "email",
                "dest@example.com",
                "Asunto",
                "Mensaje");
        NotificationResult expected = NotificationResult.success("MAIL123", "sent");
        CapturingSender sender = new CapturingSender("email", "gmail", expected);
        CapturingRegistry registry = new CapturingRegistry(sender);
        DefaultNotificationService service = new DefaultNotificationService(registry, Runnable::run);

        NotificationResult result = service.send(request);

        assertSame(expected, result);
        assertEquals("email", sender.channel());
        assertEquals("gmail", sender.provider());
        assertSame(request, registry.capturedRequest);
        assertSame(request, sender.capturedRequest);
    }

    @Test
    void shouldReturnValidationErrorWhenRequestIsNull() {
        DefaultNotificationService service = new DefaultNotificationService(new CapturingRegistry(
                new CapturingSender(NotificationResult.success("unused", "unused"))), Runnable::run);

        NotificationResult result = service.send(null);

        assertEquals(NotificationResultType.VALIDATION_ERROR, result.getType());
        assertTrue(!result.isSuccessful());
    }

    @Test
    void shouldReturnUnsupportedChannelWhenRegistryCannotResolve() {
        DefaultNotificationService service = new DefaultNotificationService(
                new ThrowingRegistry(new IllegalArgumentException("No se encontro sender compatible con channel: fax")),
                Runnable::run);

        NotificationResult result = service.send(new NotificationRequest("fax", "123", null, "hola"));

        assertEquals(NotificationResultType.UNSUPPORTED_CHANNEL, result.getType());
        assertTrue(!result.isSuccessful());
    }

    @Test
    void shouldReturnDeliveryErrorForUnexpectedRuntimeException() {
        DefaultNotificationService service = new DefaultNotificationService(
                new CapturingRegistry(new ThrowingSender(new RuntimeException("boom"))),
                Runnable::run);

        NotificationResult result = service.send(new NotificationRequest("sms", "+593999999999", null, "Codigo"));

        assertEquals(NotificationResultType.DELIVERY_ERROR, result.getType());
        assertTrue(!result.isSuccessful());
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
        private final String channel;
        private final String provider;
        private final NotificationResult result;
        private NotificationRequest capturedRequest;

        private CapturingSender(NotificationResult result) {
            this("sms", "sms", result);
        }

        private CapturingSender(String channel, String provider, NotificationResult result) {
            this.channel = channel;
            this.provider = provider;
            this.result = result;
        }

        @Override
        public String channel() {
            return channel;
        }

        @Override
        public String provider() {
            return provider;
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

    private static final class ThrowingRegistry implements NotificationSenderRegistry {
        private final RuntimeException exception;

        private ThrowingRegistry(RuntimeException exception) {
            this.exception = exception;
        }

        @Override
        public NotificationSender resolve(NotificationRequest request) {
            throw exception;
        }
    }

    private static final class ThrowingSender implements NotificationSender {
        private final RuntimeException exception;

        private ThrowingSender(RuntimeException exception) {
            this.exception = exception;
        }

        @Override
        public String channel() {
            return "sms";
        }

        @Override
        public String provider() {
            return "sms";
        }

        @Override
        public boolean supports(NotificationRequest request) {
            return true;
        }

        @Override
        public NotificationResult send(NotificationRequest request) {
            throw exception;
        }
    }
}
