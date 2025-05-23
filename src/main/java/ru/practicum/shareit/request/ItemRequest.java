package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private User requester;
    private LocalDateTime created;
}