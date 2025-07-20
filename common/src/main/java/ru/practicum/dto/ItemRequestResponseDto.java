package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;
}
