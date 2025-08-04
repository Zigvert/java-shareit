package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.UserClient;
import ru.practicum.dto.UserDto;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userClient.update(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return userClient.getAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return userClient.delete(id);
    }
}
