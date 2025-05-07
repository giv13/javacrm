package ru.giv13.javacrm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.liquibase.enabled=false")
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
