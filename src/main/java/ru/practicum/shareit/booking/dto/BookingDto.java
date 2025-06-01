package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    @Data
    public static class Item {
        private Long id;
        private String name;

        public Item(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Data
    public static class User {
        private Long id;

        public User(Long id) {
            this.id = id;
        }
    }
}