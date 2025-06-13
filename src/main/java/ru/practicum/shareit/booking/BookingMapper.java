package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.ItemSummaryDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        Long itemId = null;
        String itemName = null;
        Long bookerId = null;
        String bookerName = null;

        if (booking.getItem() != null) {
            itemId = booking.getItem().getId();
            try {
                itemName = booking.getItem().getName();
            } catch (Exception e) {
                itemName = null;
            }
        }

        if (booking.getBooker() != null) {
            bookerId = booking.getBooker().getId();
            try {
                bookerName = booking.getBooker().getName();
            } catch (Exception e) {
                bookerName = null;
            }
        }

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemSummaryDto(itemId, itemName),
                new BookerDto(bookerId, bookerName),
                booking.getStatus()
        );
    }

    public static BookingShortDto toShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker() != null ? booking.getBooker().getId() : null);
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static Booking toEntity(BookingDto dto) {
        if (dto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(dto.getStatus());
        if (dto.getItem() != null) {
            Item item = new Item();
            item.setId(dto.getItem().getId());
            item.setName(dto.getItem().getName());
            booking.setItem(item);
        }
        if (dto.getBooker() != null) {
            User booker = new User();
            booker.setId(dto.getBooker().getId());
            booker.setName(dto.getBooker().getName());
            booking.setBooker(booker);
        }
        return booking;
    }

    public static Booking toEntity(BookingCreateDto dto, Item item, User booker) {
        if (dto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}