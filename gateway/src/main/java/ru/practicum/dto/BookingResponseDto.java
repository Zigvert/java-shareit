package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserShortDto booker;
    private ItemShortDto item;

    public enum BookingStatus {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserShortDto {
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemShortDto {
        private Long id;
        private String name;
    }
}
