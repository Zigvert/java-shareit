package ru.practicum.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestCreateDto;
import ru.practicum.shareit.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> createRequest(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemRequestCreateDto requestDto
    ) {
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getOwnRequests(
            @RequestHeader(USER_HEADER) Long userId
    ) {
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getAllRequests(
            @RequestHeader(USER_HEADER) Long userId
            // убрал from и size, т.к. ItemRequestClient их не поддерживает
    ) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequestById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        return requestClient.getRequestById(userId, requestId);
    }
}
