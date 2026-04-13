# Notificacion

Libreria Java 21 para enviar notificaciones por `email`, `sms` y `push` usando una interfaz unificada, multiples proveedores por canal, eventos internos y soporte async con `CompletableFuture`.

Consulta tambien el [resumen de pruebas](TESTS_OVERVIEW.md) para ver la suite agrupada por area, prioridad y logica de negocio cubierta.

## Requisitos

- Java `21` o superior
- Maven `3.9+` recomendado

## Instalacion

### Maven

Si la libreria aun no esta publicada en un repositorio remoto, primero instalala localmente:

```bash
mvn install
```

Luego agregala a tu proyecto:

```xml
<dependency>
    <groupId>com.pruebalib</groupId>
    <artifactId>notificacion</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Gradle

Si la libreria esta instalada en tu repositorio local de Maven:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.pruebalib:notificacion:0.0.1-SNAPSHOT")
}
```

## Quick Start

### Ejecutar la demo con Docker

Si no quieres instalar Java localmente, puedes compilar y ejecutar la demo en contenedor:

```bash
docker build -t notificacion-demo .
docker run --rm notificacion-demo
```

Ejemplo minimo con `email`, `sms` y `push` del uso de la libreria:

```java
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

ExecutorService executor = Executors.newFixedThreadPool(8);

try {
    List<NotificationListener> listeners = List.of(event -> System.out.println(
            "EVENT => trackerId=" + event.getTrackerId()
                    + ", type=" + event.getType()
                    + ", provider=" + event.getProvider()
                    + ", message=" + event.getMessage()));

    NotificationService service = NotificationServiceFactory.create(
            List.of(
                    new GmailNotificationSender(new GmailConfig(
                            "app@gmail.com",
                            "gmail-app-password",
                            "app@gmail.com",
                            null,
                            587,
                            true,
                            false)),
                    new SmtpNotificationSender(new SmtpConfig(
                            "mailer@example.com",
                            "smtp-password",
                            "mailer@example.com",
                            "smtp.example.com",
                            587,
                            true,
                            false)),
                    new SmsNotificationSender(new SmsConfig(
                            "acct-123",
                            "token-xyz",
                            "+15005550006",
                            "https://api.sms-provider.local")),
                    new PushNotificationSender(new PushConfig(
                            "project-demo",
                            "push-token-demo",
                            "https://api.push-provider.local"))),
            executor,
            listeners);

    NotificationResult result = service.send(new NotificationRequest(
            "email",
            "destinatario@example.com",
            "Bienvenido",
            "Tu cuenta fue creada correctamente"));

    System.out.println(result.getType());
    System.out.println(result.getProvider());
} finally {
    executor.shutdown();
}
```

## Configuracion

Toda la configuracion se hace mediante clases Java.

### Gmail

Clase: `GmailConfig`

```java
GmailConfig config = new GmailConfig(
        "app@gmail.com",
        "gmail-app-password",
        "app@gmail.com",
        null,
        587,
        true,
        false);
```

Campos:

- `username`: cuenta de Gmail usada para autenticacion.
- `password`: app password o secreto del proveedor.
- `from`: remitente visible del correo. Si va nulo o vacio, se usa `username`.
- `host`: servidor SMTP. Si va nulo o vacio, se usa `smtp.gmail.com`.
- `port`: puerto SMTP. Si va `<= 0`, se usa `587`.
- `startTls`: habilita STARTTLS.
- `ssl`: habilita SSL directo.

### SMTP generico

Clase: `SmtpConfig`

```java
SmtpConfig config = new SmtpConfig(
        "mailer@example.com",
        "smtp-password",
        "mailer@example.com",
        "smtp.example.com",
        587,
        true,
        false);
```

Campos:

- `username`: usuario de autenticacion del servidor SMTP.
- `password`: secreto o password del servidor SMTP.
- `from`: remitente visible del correo. Si va nulo o vacio, se usa `username`.
- `host`: nombre del servidor SMTP.
- `port`: puerto del servidor SMTP.
- `startTls`: habilita STARTTLS.
- `ssl`: habilita SSL directo.

### SMS

Clase: `SmsConfig`

```java
SmsConfig config = new SmsConfig(
        "acct-123",
        "token-xyz",
        "+15005550006",
        "https://api.sms-provider.local");
```

Campos:

- `accountId`: identificador de cuenta del proveedor SMS.
- `authToken`: token o secreto de autenticacion.
- `from`: remitente o numero origen del SMS.
- `baseUrl`: URL base del proveedor SMS.

### Push

Clase: `PushConfig`

```java
PushConfig config = new PushConfig(
        "project-demo",
        "push-token-demo",
        "https://api.push-provider.local");
```

Campos:

- `projectId`: identificador del proyecto o aplicacion de push.
- `apiToken`: token de autenticacion del proveedor push.
- `baseUrl`: URL base del proveedor push.

## Proveedores soportados

### Email

- `gmail` mediante `GmailNotificationSender`
- `smtp` mediante `SmtpNotificationSender`

### SMS

- `sms` mediante `SmsNotificationSender`

### Push

- `push` mediante `PushNotificationSender`

La libreria soporta multiples proveedores por canal. Por ejemplo, puedes registrar `gmail` y `smtp` al mismo tiempo para `email`. La prioridad se resuelve por la `NotificationRoutingPolicy` configurada; por defecto se respeta el orden de registro.

## API Reference

### `NotificationService`

Contrato principal de uso.

Metodos:

- `send(NotificationRequest request)`
- `sendAsync(NotificationRequest request)`
- `sendBatch(List<NotificationRequest> requests)`
- `sendBatchAsync(List<NotificationRequest> requests)`
- `sendOrThrow(NotificationRequest request)`

Descripcion breve:

- `send(...)`: envia una sola notificacion y devuelve un `NotificationResult`.
- `sendAsync(...)`: envia una sola notificacion usando el `Executor` configurado.
- `sendBatch(...)`: envia varias notificaciones preservando el orden de entrada.
- `sendBatchAsync(...)`: ejecuta el batch usando el `Executor` configurado.
- `sendOrThrow(...)`: variante orientada a `try-catch` que transforma el resultado fallido en excepcion semantica.

### `NotificationRequest`

Representa una notificacion de entrada.

Campos principales:

- `channel`: canal destino, por ejemplo `email`, `sms` o `push`.
- `recipient`: destinatario del envio.
- `subject`: asunto o titulo. Se usa sobre todo en `email` y `push`.
- `message`: cuerpo principal del mensaje.
- `data`: metadata adicional clave/valor, util por ejemplo para payloads push.

### `NotificationResult`

Resultado del envio.

Campos principales:

- `type`: tipo de resultado (`SUCCESS`, `VALIDATION_ERROR`, `DELIVERY_ERROR`, etc.).
- `successful`: indica si el envio fue exitoso.
- `channel`: canal resuelto para el envio.
- `provider`: proveedor que atendio el envio.
- `providerMessageId`: identificador devuelto por el proveedor cuando existe.
- `errorCode`: codigo funcional o tecnico del fallo.
- `description`: mensaje resumido orientado a consumo funcional.
- `technicalMessage`: detalle tecnico del fallo cuando aplica.

### `NotificationServiceFactory`

Punto recomendado para construir el servicio.

Metodos utiles:

- `create(List<NotificationSender> senders)`
- `create(List<NotificationSender> senders, Executor executor)`
- `create(List<NotificationSender> senders, Executor executor, List<NotificationListener> listeners)`

Descripcion breve:

- `create(senders)`: construye el servicio con executor directo, util para pruebas o ejecucion simple.
- `create(senders, executor)`: permite controlar asincronia y ciclo de vida desde la aplicacion cliente.
- `create(senders, executor, listeners)`: agrega suscripcion a eventos internos.

### `NotificationListener`

Permite suscribirse a eventos internos del ciclo de envio.

Eventos disponibles:

- `SEND_STARTED`
- `SEND_SUCCEEDED`
- `SEND_FAILED`
- `VALIDATION_FAILED`

Cada `NotificationEvent` expone un `trackerId` unico por envio para correlacionar los eventos de una misma operacion.

Campos principales de `NotificationEvent`:

- `type`: tipo de evento publicado.
- `request`: request original asociado al envio.
- `result`: resultado disponible en eventos de exito o fallo.
- `provider`: proveedor involucrado en ese paso del flujo.
- `message`: descripcion resumida del evento.
- `trackerId`: identificador unico para correlacionar todos los eventos del mismo envio.

## Envio async y batch

### Async

`sendAsync(...)` y `sendBatchAsync(...)` usan el `Executor` configurado en el factory.

Recomendacion:

- en produccion, entrega un `ExecutorService` controlado por la aplicacion
- limita la concurrencia si vas a hablar con proveedores externos
- cierra ese executor cuando termine el ciclo de vida de tu proceso

### Batch

`sendBatch(...)`:

- preserva el orden de entrada
- procesa cada request usando el flujo normal de `send(...)`
- tolera fallos individuales devolviendo un `NotificationResult` por cada request

## Seguridad

Buenas practicas recomendadas:

- No hardcodees credenciales en codigo fuente.
- Carga `passwords`, `tokens` y `api keys` desde variables de entorno o un secret manager.
- No registres credenciales en logs ni en mensajes de error.
- Usa cuentas tecnicas separadas por entorno.
- Rota credenciales periodicamente.
- Limita permisos por proveedor al minimo necesario.
- Si entregas un `ExecutorService` desde la aplicacion, cierra correctamente su ciclo de vida.

## Nota sobre la demo

Puedes revisar [NotificationLibraryDemo.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/main/java/com/pruebalib/notification/NotificationLibraryDemo.java:1) para ver un ejemplo completo de:

- registro de varios providers
- suscripcion a eventos
- uso sync y batch async
- apertura y cierre correcto del `ExecutorService`

## Navegacion adicional

- [Resumen de pruebas](TESTS_OVERVIEW.md)
