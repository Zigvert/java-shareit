package ru.practicum.shareit.request.mapper;

import ru.practicum.dto.ItemShortDto;
import ru.practicum.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestResponseDto toDto(ItemRequest request, List<ItemShortDto> items) {
        ItemRequestResponseDto dto = new ItemRequestResponseDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items);
        return dto;
    }
}
