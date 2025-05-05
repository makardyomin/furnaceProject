package com.example.petproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PetProjectApplicationTests {

    @Test
    void contextLoads() {
        // Проверка загрузки контекста Spring Boot, если тест проходит, то приложение запускается без ошибок
    }

    @Test
    void mainMethodRuns() {
        // Вызов main-метода для проверки его работы
        PetProjectApplication.main(new String[]{});
    }
}

