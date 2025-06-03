package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден с id: " + itemId));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем предмета");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto get(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден с id: " + itemId));
        ItemDto itemDto = itemMapper.toDto(item);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            itemDto.setLastBooking(bookingRepository.findLastBooking(itemId, now).stream()
                    .findFirst().map(bookingMapper::toShortDto).orElse(null));
            itemDto.setNextBooking(bookingRepository.findNextBooking(itemId, now).stream()
                    .findFirst().map(bookingMapper::toShortDto).orElse(null));
        }

        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toDto).collect(Collectors.toList()));
        return itemDto;
    }

    public ItemDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден с id: " + itemId));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId, Integer from, Integer size) {
        List<Item> items = itemRepository.findAllByOwnerId(userId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        return items.stream().map(item -> {
            ItemDto dto = itemMapper.toDto(item);
            dto.setLastBooking(bookingRepository.findLastBooking(item.getId(), now).stream()
                    .findFirst().map(bookingMapper::toShortDto).orElse(null));
            dto.setNextBooking(bookingRepository.findNextBooking(item.getId(), now).stream()
                    .findFirst().map(bookingMapper::toShortDto).orElse(null));
            dto.setComments(commentRepository.findAllByItemId(item.getId()).stream()
                    .map(commentMapper::toDto).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.search(text).stream()
                .skip(from)
                .limit(size)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
