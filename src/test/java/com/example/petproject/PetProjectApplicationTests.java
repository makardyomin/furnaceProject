package com.example.petproject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PetProjectApplicationTests {

    @Test
    void contextLoads() {
        // Проверка загрузки контекста Spring Boot
        // Если контекст загрузился, то класс приложения не равен null
        assertNotNull(PetProjectApplication.class, "Контекст не загрузился, PetProjectApplication не должен быть null");
    }

    @Test
    void mainMethodRuns() {
        // Проверка, что main-метод запускается без исключений.
        assertDoesNotThrow(() -> PetProjectApplication.main(new String[]{}), "Main method threw an exception");
    }
}

