package com.pruebalib.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaludoTest {

    @Test
    void greetReturnsCapitalizedMessage() {
        Saludo library = new Saludo();
        String result = library.greet("mundo");

        assertEquals("Hola MUNDO desde la librería", result);
    }
}
