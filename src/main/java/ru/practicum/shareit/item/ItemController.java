package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long id,
                                          @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.update(userId, id, itemDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getAll(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam String text) {
        return ResponseEntity.ok(itemService.search(text));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }
}