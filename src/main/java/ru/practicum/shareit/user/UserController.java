package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    // Создать пользователя
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        // Проверка уникальности email
        if (user.getEmail() != null && emailExists(user.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email уже используется");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        user.setId(idCounter++);
        users.put(user.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // Получить пользователя по id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        User user = users.get(id);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Пользователь не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(user);
    }

    // Получить всех пользователей
    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        return ResponseEntity.ok(users.values());
    }

    // Частично обновить пользователя
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = users.get(id);
        if (existingUser == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Пользователь не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Проверяем email если он меняется
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (emailExists(user.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email уже используется");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        return ResponseEntity.ok(existingUser);
    }

    // Удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User removed = users.remove(id);
        if (removed == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Пользователь не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok().build();
    }

    // Вспомогательный метод для проверки уникальности email
    private boolean emailExists(String email) {
        if (email == null) return false;
        return users.values().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
    }
}