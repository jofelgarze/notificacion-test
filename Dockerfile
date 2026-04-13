FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package dependency:copy-dependencies -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/notificacion-0.0.1-SNAPSHOT.jar /app/notificacion.jar
COPY --from=build /app/target/dependency /app/dependency

CMD ["java", "-cp", "/app/notificacion.jar:/app/dependency/*", "com.pruebalib.notification.NotificationLibraryDemo"]
