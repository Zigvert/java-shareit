package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ShareItApp.class)
@ActiveProfiles("test")
class ShareItTests {

	@Test
	void contextLoads() {
		// Проверяет, что контекст Spring Boot успешно загружается
	}
}