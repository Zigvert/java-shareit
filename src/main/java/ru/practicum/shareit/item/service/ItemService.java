package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemCreateDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto);

    ItemDto getById(Long userId, Long itemId);

    List<ItemDto> getAllByOwner(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}