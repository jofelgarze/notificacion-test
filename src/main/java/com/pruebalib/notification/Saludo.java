package com.pruebalib.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class Saludo {

    public String greet(@NonNull String name) {
        String normalized = name.trim().toUpperCase();
        log.info("Generando saludo para {}", normalized);
        return "Hola " + normalized + " desde la librería";
    }
}
