package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.ItemRequestCreateDto;
import ru.practicum.shareit.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestControllerServer {

    private final ItemRequestService itemRequestService;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDto createRequest(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody ItemRequestCreateDto requestDto
    ) {
        // Обратите внимание: здесь нет @Valid и валидации, предполагается, что gateway уже проверил
        return itemRequestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getOwnRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
