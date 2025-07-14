package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "Start date must not be null")
    @Future(message = "Start date must be in the future")
    private LocalDateTime start;

    @NotNull(message = "End date must not be null")
    @Future(message = "End date must be in the future")
    private LocalDateTime end;

    @NotNull(message = "Item ID must not be null")
    private Long itemId;

    private Long bookerId;
    private BookingStatus status;
}
