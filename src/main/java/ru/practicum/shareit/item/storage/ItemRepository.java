package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
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

    public List<Item> findAllByOwnerId(Long ownerId, Pageable pageable) {
        List<Item> ownerItems = items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), ownerItems.size());
        return start < ownerItems.size() ? ownerItems.subList(start, end) : Collections.emptyList();
    }

    public List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
            String name, String description, boolean available, Pageable pageable) {
        List<Item> matchingItems = items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable() == available &&
                        ((item.getName() != null && item.getName().toLowerCase().contains(name.toLowerCase())) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(description.toLowerCase()))))
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), matchingItems.size());
        return start < matchingItems.size() ? matchingItems.subList(start, end) : Collections.emptyList();
    }

    public void delete(Long id) {
        items.remove(id);
    }
}