package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingCreateDto bookingDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto get(Long userId, Long bookingId);

    List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size);
}