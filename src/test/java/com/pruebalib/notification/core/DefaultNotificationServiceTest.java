package com.pruebalib.notification.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationResultType;
import com.pruebalib.notification.common.exception.NotificationConfigurationException;
import com.pruebalib.notification.common.exception.NotificationValidationException;
import com.pruebalib.notification.common.exception.UnsupportedChannelException;
import com.pruebalib.notification.spi.NotificationSender;
import com.pruebalib.notification.spi.NotificationSenderRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        assertEquals("REQUEST_NULL", result.getErrorCode());
    }

    @Test
    void shouldReturnUnsupportedChannelWhenRegistryCannotResolve() {
        DefaultNotificationService service = new DefaultNotificationService(
                new ThrowingRegistry(
                        new UnsupportedChannelException("No se encontro sender compatible con channel: fax")),
                Runnable::run);

        NotificationResult result = service.send(new NotificationRequest("fax", "123", null, "hola"));

        assertEquals(NotificationResultType.UNSUPPORTED_CHANNEL, result.getType());
        assertTrue(!result.isSuccessful());
        assertEquals("UNSUPPORTED_CHANNEL", result.getErrorCode());
    }

    @Test
    void shouldReturnDeliveryErrorForUnexpectedRuntimeException() {
        DefaultNotificationService service = new DefaultNotificationService(
                new CapturingRegistry(new ThrowingSender(new RuntimeException("boom"))),
                Runnable::run);

        NotificationResult result = service.send(new NotificationRequest("sms", "+593999999999", null, "Codigo"));

        assertEquals(NotificationResultType.DELIVERY_ERROR, result.getType());
        assertTrue(!result.isSuccessful());
        assertEquals("UNEXPECTED_ERROR", result.getErrorCode());
    }

    @Test
    void shouldMapValidationExceptionFromSender() {
        DefaultNotificationService service = new DefaultNotificationService(
                new CapturingRegistry(new ThrowingSender(new NotificationValidationException("recipient invalido"))),
                Runnable::run);

        NotificationResult result = service.send(new NotificationRequest("sms", "+593999999999", null, "Codigo"));

        assertEquals(NotificationResultType.VALIDATION_ERROR, result.getType());
        assertEquals("VALIDATION_ERROR", result.getErrorCode());
        assertEquals("recipient invalido", result.getTechnicalMessage());
    }

    @Test
    void shouldSupportSendOrThrow() {
        DefaultNotificationService service = new DefaultNotificationService(
                new CapturingRegistry(new ThrowingSender(new NotificationConfigurationException("config invalida"))),
                Runnable::run);

        assertThrows(NotificationConfigurationException.class,
                () -> service.sendOrThrow(new NotificationRequest("sms", "+593999999999", null, "Codigo")));
    }

    @Test
    void shouldPublishStartedAndSucceededEvents() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo");
        NotificationResult expected = NotificationResult.success("SM123", "queued");
        CapturingSender sender = new CapturingSender(expected);
        List<NotificationEvent> events = new ArrayList<>();
        DefaultNotificationService service = new DefaultNotificationService(
                new CapturingRegistry(sender),
                Runnable::run,
                List.of(events::add));

        NotificationResult result = service.send(request);

        assertTrue(result.isSuccessful());
        assertEquals(2, events.size());
        assertEquals(NotificationEventType.SEND_STARTED, events.get(0).getType());
        assertEquals(NotificationEventType.SEND_SUCCEEDED, events.get(1).getType());
    }

    @Test
    void shouldPublishValidationFailedEvent() {
        List<NotificationEvent> events = new ArrayList<>();
        DefaultNotificationService service = new DefaultNotificationService(
                new CapturingRegistry(new CapturingSender(NotificationResult.success("unused", "unused"))),
                Runnable::run,
                List.of(events::add));

        NotificationResult result = service.send(null);

        assertEquals(NotificationResultType.VALIDATION_ERROR, result.getType());
        assertEquals(1, events.size());
        assertEquals(NotificationEventType.VALIDATION_FAILED, events.get(0).getType());
    }

    @Test
    void shouldPublishSendFailedEvent() {
        NotificationRequest request = new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Codigo");
        NotificationResult failure = NotificationResult.failure(
                NotificationResultType.DELIVERY_ERROR,
                "sms",
                "sms",
                "DELIVERY_ERROR",
                "fallo el envio",
                "fallo el envio");
        List<NotificationEvent> events = new ArrayList<>();
        DefaultNotificationService service = new DefaultNotificationService(
                new CapturingRegistry(new CapturingSender(failure)),
                Runnable::run,
                List.of(events::add));

        NotificationResult result = service.send(request);

        assertEquals(NotificationResultType.DELIVERY_ERROR, result.getType());
        assertEquals(2, events.size());
        assertEquals(NotificationEventType.SEND_STARTED, events.get(0).getType());
        assertEquals(NotificationEventType.SEND_FAILED, events.get(1).getType());
    }

    @Test
    void shouldPublishEventsDuringFallback() {
        NotificationRequest request = new NotificationRequest(
                "email",
                "dest@example.com",
                "Asunto",
                "Mensaje");

        NotificationSender first = new CapturingSender(
                "email",
                "gmail",
                NotificationResult.failure(
                        NotificationResultType.DELIVERY_ERROR,
                        "email",
                        "gmail",
                        "DELIVERY_ERROR",
                        "gmail failed",
                        "gmail failed"));
        NotificationSender second = new CapturingSender(
                "email",
                "smtp",
                NotificationResult.success("email", "smtp", "smtp-123", "sent"));
        List<NotificationEvent> events = new ArrayList<>();

        DefaultNotificationService service = new DefaultNotificationService(
                new MultiSenderRegistry(first, second),
                Runnable::run,
                List.of(events::add));

        NotificationResult result = service.send(request);

        assertTrue(result.isSuccessful());
        assertEquals(4, events.size());
        assertEquals(NotificationEventType.SEND_STARTED, events.get(0).getType());
        assertEquals("gmail", events.get(0).getProvider());
        assertEquals(NotificationEventType.SEND_FAILED, events.get(1).getType());
        assertEquals("gmail", events.get(1).getProvider());
        assertEquals(NotificationEventType.SEND_STARTED, events.get(2).getType());
        assertEquals("smtp", events.get(2).getProvider());
        assertEquals(NotificationEventType.SEND_SUCCEEDED, events.get(3).getType());
        assertEquals("smtp", events.get(3).getProvider());
    }

    @Test
    void shouldFallbackToSecondProviderWhenFirstReturnsDeliveryError() {
        NotificationRequest request = new NotificationRequest(
                "email",
                "dest@example.com",
                "Asunto",
                "Mensaje");

        NotificationSender first = new CapturingSender(
                "email",
                "gmail",
                NotificationResult.failure(
                        NotificationResultType.DELIVERY_ERROR,
                        "email",
                        "gmail",
                        "DELIVERY_ERROR",
                        "gmail failed",
                        "gmail failed"));
        NotificationSender second = new CapturingSender(
                "email",
                "smtp",
                NotificationResult.success("email", "smtp", "smtp-123", "sent"));

        DefaultNotificationService service = new DefaultNotificationService(
                new MultiSenderRegistry(first, second),
                Runnable::run);

        NotificationResult result = service.send(request);

        assertTrue(result.isSuccessful());
        assertEquals("smtp", result.getProvider());
        assertEquals("smtp-123", result.getProviderMessageId());
    }

    @Test
    void shouldUseCustomRoutingPolicyOrder() {
        NotificationRequest request = new NotificationRequest(
                "email",
                "dest@example.com",
                "Asunto",
                "Mensaje");

        NotificationSender first = new CapturingSender(
                "email",
                "gmail",
                NotificationResult.success("email", "gmail", "gmail-123", "sent"));
        NotificationSender second = new CapturingSender(
                "email",
                "smtp",
                NotificationResult.success("email", "smtp", "smtp-123", "sent"));

        NotificationRoutingPolicy reversePolicy = (ignoredRequest, candidates) -> List.of(candidates.get(1),
                candidates.get(0));

        DefaultNotificationService service = new DefaultNotificationService(
                new MultiSenderRegistry(first, second),
                Runnable::run,
                List.of(),
                reversePolicy);

        NotificationResult result = service.send(request);

        assertTrue(result.isSuccessful());
        assertEquals("smtp", result.getProvider());
        assertEquals("smtp-123", result.getProviderMessageId());
    }

    @Test
    void shouldNotFallbackWhenFirstReturnsValidationError() {
        NotificationRequest request = new NotificationRequest(
                "email",
                "dest@example.com",
                "Asunto",
                "Mensaje");

        NotificationSender first = new CapturingSender(
                "email",
                "gmail",
                NotificationResult.failure(
                        NotificationResultType.VALIDATION_ERROR,
                        "email",
                        "gmail",
                        "VALIDATION_ERROR",
                        "invalid request",
                        "invalid request"));
        NotificationSender second = new CapturingSender(
                "email",
                "smtp",
                NotificationResult.success("email", "smtp", "smtp-123", "sent"));

        DefaultNotificationService service = new DefaultNotificationService(
                new MultiSenderRegistry(first, second),
                Runnable::run);

        NotificationResult result = service.send(request);

        assertEquals(NotificationResultType.VALIDATION_ERROR, result.getType());
        assertEquals("gmail", result.getProvider());
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

        @Override
        public java.util.List<NotificationSender> resolveAll(NotificationRequest request) {
            this.capturedRequest = request;
            return java.util.List.of(sender);
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

        @Override
        public java.util.List<NotificationSender> resolveAll(NotificationRequest request) {
            throw exception;
        }
    }

    private static final class MultiSenderRegistry implements NotificationSenderRegistry {
        private final java.util.List<NotificationSender> senders;

        private MultiSenderRegistry(NotificationSender... senders) {
            this.senders = java.util.List.of(senders);
        }

        @Override
        public NotificationSender resolve(NotificationRequest request) {
            return senders.getFirst();
        }

        @Override
        public java.util.List<NotificationSender> resolveAll(NotificationRequest request) {
            return senders;
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
