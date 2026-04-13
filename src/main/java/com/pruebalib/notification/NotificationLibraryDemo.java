package com.pruebalib.notification;

import java.util.List;

import com.pruebalib.notification.api.NotificationRequest;
import com.pruebalib.notification.api.NotificationResult;
import com.pruebalib.notification.api.NotificationService;
import com.pruebalib.notification.core.NotificationListener;
import com.pruebalib.notification.core.NotificationServiceFactory;
import com.pruebalib.notification.provider.gmail.GmailConfig;
import com.pruebalib.notification.provider.gmail.GmailNotificationSender;
import com.pruebalib.notification.provider.push.PushConfig;
import com.pruebalib.notification.provider.push.PushNotificationSender;
import com.pruebalib.notification.provider.sms.SmsConfig;
import com.pruebalib.notification.provider.sms.SmsNotificationSender;
import com.pruebalib.notification.provider.smtp.SmtpConfig;
import com.pruebalib.notification.provider.smtp.SmtpNotificationSender;
import com.pruebalib.notification.spi.NotificationSender;

public final class NotificationLibraryDemo {

    private NotificationLibraryDemo() {
    }

    public static void main(String[] args) {
        NotificationService service = createNotificationService();

        NotificationResult emailResult = service.send(exampleEmailRequest());
        NotificationResult smsResult = service.send(exampleSmsRequest());
        NotificationResult pushResult = service.send(examplePushRequest());

        printResult("EMAIL", emailResult);
        printResult("SMS", smsResult);
        printResult("PUSH", pushResult);
    }

    public static NotificationService createNotificationService() {
        return NotificationServiceFactory.create(configuredSenders(), configuredListeners());
    }

    public static List<NotificationSender> configuredSenders() {
        return List.of(
                new GmailNotificationSender(exampleGmailConfig()),
                new SmtpNotificationSender(exampleSmtpConfig()),
                new SmsNotificationSender(exampleSmsConfig()),
                new PushNotificationSender(examplePushConfig()));
    }

    public static List<NotificationListener> configuredListeners() {
        return List.of(event -> System.out.println(
                "EVENT => trackerId=" + event.getTrackerId()
                        + ", type=" + event.getType()
                        + ", provider=" + event.getProvider()
                        + ", message=" + event.getMessage()));
    }

    public static NotificationRequest exampleEmailRequest() {
        return new NotificationRequest(
                "email",
                "destinatario@example.com",
                "Bienvenido",
                "Tu cuenta fue creada correctamente");
    }

    public static NotificationRequest exampleSmsRequest() {
        return new NotificationRequest(
                "sms",
                "+593999999999",
                null,
                "Tu codigo de verificacion es 123456");
    }

    public static NotificationRequest examplePushRequest() {
        return new NotificationRequest(
                "push",
                "abcdef1234567890",
                "Nueva promocion",
                "Tienes un descuento disponible");
    }

    /**
     * En un escenario real, estas configuraciones
     * probablemente se cargarían desde un archivo de propiedades,
     * variables de entorno, o un servicio de gestión de secretos.
     * Nota: con credenciales validas el correo se se envia.
     */
    public static GmailConfig exampleGmailConfig() {
        return new GmailConfig(
                "app@gmail.com",
                "gmail-app-password",
                "app@gmail.com",
                null,
                587,
                true,
                false);
    }

    public static SmtpConfig exampleSmtpConfig() {
        return new SmtpConfig(
                "mailer@example.com",
                "smtp-password",
                "mailer@example.com",
                "smtp.example.com",
                587,
                true,
                false);
    }

    public static SmsConfig exampleSmsConfig() {
        return new SmsConfig(
                "acct-123",
                "token-xyz",
                "+15005550006",
                "https://api.sms-provider.local");
    }

    public static PushConfig examplePushConfig() {
        return new PushConfig(
                "project-demo",
                "push-token-demo",
                "https://api.push-provider.local");
    }

    private static void printResult(String label, NotificationResult result) {
        System.out.println(label + " => "
                + "successful=" + result.isSuccessful()
                + ", type=" + result.getType()
                + ", provider=" + result.getProvider()
                + ", messageId=" + result.getProviderMessageId()
                + ", description=" + result.getDescription());
    }
}
