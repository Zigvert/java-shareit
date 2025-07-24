package ru.practicum.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;

    @NotNull(message = "Start date must not be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime start;

    @NotNull(message = "End date must not be null")
    @Future(message = "End date must be in the future")
    private LocalDateTime end;

    @NotNull(message = "Item ID must not be null")
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;

    public enum BookingStatus {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
