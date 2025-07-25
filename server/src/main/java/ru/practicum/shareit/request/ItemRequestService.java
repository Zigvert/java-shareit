package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto create(Long userId, ItemRequestCreateDto dto);

    List<ItemRequestResponseDto> getOwnRequests(Long userId);

    List<ItemRequestResponseDto> getAllRequests(Long userId);

    ItemRequestResponseDto getRequestById(Long userId, Long requestId);
}
