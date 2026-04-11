package com.pruebalib.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LibraryTest {

    @Test
    void greetReturnsCapitalizedMessage() {
        Library library = new Library();
        String result = library.greet("mundo");

        assertEquals("Hola Mundo desde la librería", result);
    }
}
