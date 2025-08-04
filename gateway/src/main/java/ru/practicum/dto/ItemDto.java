package ru.practicum.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingDto {
        private Long id;
        private Long bookerId;
    }
}
