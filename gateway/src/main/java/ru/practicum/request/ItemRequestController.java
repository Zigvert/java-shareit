package ru.practicum.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDto createRequest(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemRequestCreateDto requestDto
    ) {
        return requestClient.createRequest(userId, requestDto).getBody();
    }

    @GetMapping
    public List<ItemRequestResponseDto> getOwnRequests(
            @RequestHeader(USER_HEADER) Long userId
    ) {
        return requestClient.getOwnRequests(userId).getBody();
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(
            @RequestHeader(USER_HEADER) Long userId
    ) {
        return requestClient.getAllRequests(userId).getBody();
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        return requestClient.getRequestById(userId, requestId).getBody();
    }
}
