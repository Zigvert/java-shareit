package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    private Long id;

    @NotBlank(message = "Comment text must not be blank")
    private String text;

    private String authorName;
    private String created;
}
