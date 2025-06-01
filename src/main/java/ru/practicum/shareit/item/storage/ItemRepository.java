package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public Collection<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        ((item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        items.remove(id);
    }
}