package ru.practicum.shareit.booking.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookingRepository {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private long nextId = 1;

    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(nextId++);
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public List<Booking> findAllByBookerId(Long bookerId, String state, int from, int size) {
        List<Booking> result = bookings.values().stream()
                .filter(b -> b.getBooker().getId().equals(bookerId))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .collect(Collectors.toList());

        return filterByState(result, state, from, size);
    }

    public List<Booking> findAllByOwnerId(Long ownerId, String state, int from, int size) {
        List<Booking> result = bookings.values().stream()
                .filter(b -> b.getItem().getOwner().getId().equals(ownerId))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .collect(Collectors.toList());

        return filterByState(result, state, from, size);
    }

    public List<Booking> findLastBooking(Long itemId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getEnd().isBefore(now))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .sorted((b1, b2) -> b2.getEnd().compareTo(b1.getEnd()))
                .limit(1)
                .collect(Collectors.toList());
    }

    public List<Booking> findNextBooking(Long itemId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getStart().isAfter(now))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .sorted(Comparator.comparing(Booking::getStart))
                .limit(1)
                .collect(Collectors.toList());
    }

    public List<Booking> findAllByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(b -> b.getBooker().getId().equals(bookerId))
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getEnd().isBefore(now))
                .collect(Collectors.toList());
    }

    private List<Booking> filterByState(List<Booking> bookings, String state, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> filtered;
        switch (state.toUpperCase()) {
            case "ALL":
                filtered = bookings;
                break;
            case "CURRENT":
                filtered = bookings.stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case "PAST":
                filtered = bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                filtered = bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                filtered = bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
                break;
            case "REJECTED":
                filtered = bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return filtered.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }
}