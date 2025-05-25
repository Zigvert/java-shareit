package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = getUserOrThrow(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        User user = getUserOrThrow(userId);
        Item existingItem = getItemOrThrow(itemId);

        if (!existingItem.getOwner().getId().equals(user.getId())) {
            throw new NotFoundException("User is not the owner of the item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = getItemOrThrow(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        return user;
    }

    private Item getItemOrThrow(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found with id: " + itemId);
        }
        return item;
    }
}
