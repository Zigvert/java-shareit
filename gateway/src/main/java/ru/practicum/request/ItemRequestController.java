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
    public ResponseEntity<ItemRequestResponseDto> createRequest(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemRequestCreateDto requestDto
    ) {
        ResponseEntity<ItemRequestResponseDto> response = requestClient.createRequest(userId, requestDto);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getOwnRequests(
            @RequestHeader(USER_HEADER) Long userId
    ) {
        ResponseEntity<List<ItemRequestResponseDto>> response = requestClient.getOwnRequests(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getAllRequests(
            @RequestHeader(USER_HEADER) Long userId
    ) {
        ResponseEntity<List<ItemRequestResponseDto>> response = requestClient.getAllRequests(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequestById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        ResponseEntity<ItemRequestResponseDto> response = requestClient.getRequestById(userId, requestId);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
