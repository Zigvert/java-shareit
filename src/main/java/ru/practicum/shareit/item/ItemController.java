package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;

import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;
    private final UserController userController;

    public ItemController(UserController userController) {
        this.userController = userController;
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                        @Valid @RequestBody Item item) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "X-Sharer-User-Id header is required"));
        }
        User user = userController.getAllUsers().getBody().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        item.setId(idCounter++);
        item.setOwner(user);
        items.put(item.getId(), item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id) {
        Item item = items.get(id);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item not found"));
        }
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<Collection<Item>> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        User user = userController.getAllUsers().getBody().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        Collection<Item> userItems = items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userItems);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long id,
                                        @RequestBody Item item) {
        Item existingItem = items.get(id);
        if (existingItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item not found"));
        }
        User user = userController.getAllUsers().getBody().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        if (!existingItem.getOwner().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "User is not the owner"));
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        return ResponseEntity.ok(existingItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        Item removed = items.remove(id);
        if (removed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item not found"));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Item>> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam String text) {
        User user = userController.getAllUsers().getBody().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList()); // Исправлено: 200 и пустой список
        }
        Collection<Item> foundItems = items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(foundItems);
    }
}