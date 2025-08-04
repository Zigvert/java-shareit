package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserShortDtoTest {

    @Test
    void testLombokGeneratedMethods() {
        UserShortDto dto = new UserShortDto(123L);
        assertThat(dto.getId()).isEqualTo(123L);

        // Проверим сеттеры (хотя @Data обычно не генерирует сеттеры для final полей, но у тебя обычное поле)
        dto.setId(456L);
        assertThat(dto.getId()).isEqualTo(456L);
    }
}
