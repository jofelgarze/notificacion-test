# Test Suite Overview

Volver al [README](README.md).

## Objetivo

Este documento resume la suite de pruebas de la librería, agrupada por área funcional. La intención no es solo listar clases de test, sino explicar qué lógica de negocio protege cada bloque y qué prioridad tiene dentro del sistema.

La prioridad está clasificada así:

- `Crítica`: protege el contrato principal o el flujo central de negocio.
- `Alta`: protege componentes estructurales o reglas transversales relevantes.
- `Media`: protege integraciones o comportamientos importantes pero más acotados.

## Core Service

Esta sección valida el comportamiento más importante de la librería: la orquestación del envío. Aquí se comprueba que el servicio resuelva senders, publique eventos, maneje errores semánticos, aplique fallback cuando corresponde y soporte async y batch sin romper el contrato principal.

| Clase de prueba | Prioridad | Casos que cubre |
|---|---|---|
| [DefaultNotificationServiceTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/core/DefaultNotificationServiceTest.java:1) | `Crítica` | Flujo principal de `send`, `sendAsync`, `sendBatch`, `sendBatchAsync`, request nulo, channel no soportado, mapeo de excepciones a resultados, `sendOrThrow`, publicación de eventos, `trackerId`, fallback entre providers, prioridad configurable y preservación del provider en fallos. |
| [AbstractNotificationSenderTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/core/AbstractNotificationSenderTest.java:1) | `Media` | Comportamiento común heredado por los senders: `supports(...)` cuando el canal coincide, cuando no coincide y cuando el request es nulo. Protege la base común para no repetir tests equivalentes en cada sender. |

## Bootstrap y Resolución

Esta sección valida cómo se construye el servicio y cómo se resuelven los senders registrados. La lógica de negocio que se protege aquí es que la librería arranque con wiring correcto, rechace configuraciones inválidas y pueda soportar múltiples proveedores por canal sin ambigüedad.

| Clase de prueba | Prioridad | Casos que cubre |
|---|---|---|
| [NotificationServiceFactoryTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/core/NotificationServiceFactoryTest.java:1) | `Alta` | Creación del servicio por lista y varargs, rechazo de lista vacía, validación de duplicados por `channel + provider`, soporte de varios providers del mismo canal y rechazo de `routingPolicy` nula. |
| [InMemoryNotificationSenderRegistryTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/core/InMemoryNotificationSenderRegistryTest.java:1) | `Alta` | Resolución del sender compatible para distintos canales, prioridad del primer provider registrado y excepción cuando no existe sender compatible. Protege el comportamiento del registry en memoria. |

## Validación

Esta sección protege la lógica de validación desacoplada. El objetivo de negocio aquí es garantizar que la librería rechace requests inválidos antes de tratarlos como errores de entrega, y que las reglas de cada canal queden expresadas de forma consistente y testeable.

| Clase de prueba | Prioridad | Casos que cubre |
|---|---|---|
| [ChannelNotificationValidatorTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/core/validation/ChannelNotificationValidatorTest.java:1) | `Alta` | Validación correcta e inválida para `email`, `sms` y `push`, incluyendo formatos de recipient propios de cada canal. Protege el subsistema de validación desacoplada. |

## Providers de Email

Esta sección valida el comportamiento concreto de los providers de email. La lógica de negocio que se pone a prueba es que el sender construya correctamente el payload, use el cliente adecuado y devuelva un resultado consistente a partir de la integración SMTP/Gmail.

| Clase de prueba | Prioridad | Casos que cubre |
|---|---|---|
| [GmailNotificationSenderTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/provider/gmail/GmailNotificationSenderTest.java:1) | `Media` | Construcción de payload, uso del cliente Gmail y retorno del resultado exitoso esperado. |
| [SmtpNotificationSenderTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/provider/smtp/SmtpNotificationSenderTest.java:1) | `Media` | Construcción de payload, uso del cliente SMTP y retorno del resultado exitoso esperado. |

## Providers de Mensajería

Esta sección protege el comportamiento funcional de SMS y Push, donde además del armado del payload interesa verificar cómo se interpretan respuestas del proveedor y cómo se representan fallos controlados sin depender de conexiones reales.

| Clase de prueba | Prioridad | Casos que cubre |
|---|---|---|
| [SmsNotificationSenderTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/provider/sms/SmsNotificationSenderTest.java:1) | `Media` | Construcción de payload SMS, invocación del cliente, interpretación de respuesta exitosa del proveedor y representación de fallos controlados de entrega. |
| [PushNotificationSenderTest.java](/c:/Proyectos/estudios/autocapacitacion/Java/src/test/java/com/pruebalib/notification/provider/push/PushNotificationSenderTest.java:1) | `Media` | Construcción de payload Push, invocación del cliente, interpretación de respuesta aceptada y manejo de fallos controlados del proveedor. |

## Resumen de Cobertura

La suite está más concentrada en el `core` que en los providers individuales. Eso es intencional: el mayor riesgo del proyecto está en la orquestación, el routing, el fallback, los eventos, el manejo de errores y el contrato público, no en repetir decenas de variantes similares por cada provider.

En términos prácticos, las áreas más protegidas hoy son:

- flujo principal del servicio
- construcción y wiring del sistema
- validación desacoplada
- eventos y correlación por `trackerId`
- async y batch

Las áreas relativamente más ligeras son:

- Gmail
- SMTP

Eso no significa que estén desatendidas, sino que su cobertura actual es proporcional al riesgo que tienen dentro del diseño.

Volver al [README](README.md).
