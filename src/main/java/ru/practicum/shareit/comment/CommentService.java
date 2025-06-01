package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentCreateDto;

public interface CommentService {
    CommentDto create(Long userId, Long itemId, CommentCreateDto commentDto);
}