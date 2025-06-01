package ru.practicum.shareit.comment.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class CommentCreateDto {
    @NotBlank(message = "Comment text must not be blank")
    private String text;
}