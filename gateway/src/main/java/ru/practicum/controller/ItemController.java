package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemCreateDto;
import ru.practicum.dto.ItemDto;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemCreateDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.getAllByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
