package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;

    @Getter
    @Setter
    public static class ItemShortDto {
        private Long id;
        private String name;
    }
}
