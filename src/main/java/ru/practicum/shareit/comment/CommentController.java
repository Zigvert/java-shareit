package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/items/{itemId}/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @Valid @RequestBody CommentCreateDto commentDto) {
        return commentService.create(userId, itemId, commentDto);
    }
}