package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingCreateDto bookingDto) {
        // Проверка существования пользователя
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        // Проверка существования предмета
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + bookingDto.getItemId() + " not found"));

        // Проверка доступности предмета
        if (!item.getAvailable()) {
            throw new ValidationException("Item with id " + item.getId() + " is not available");
        }

        // Проверка, что владелец не может бронировать свой предмет
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book their own item");
        }

        // Валидация дат
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Start and end dates must not be null");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Start date must be before end date");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }

        // Создание бронирования
        Booking booking = BookingMapper.toEntity(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        try {
            Booking saved = bookingRepository.save(booking);
            return BookingMapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save booking: " + e.getMessage(), e);
        }
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only the item owner can approve the booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking is already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("User is neither booker nor owner");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByBookerId(userId, now, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByBookerId(userId, now, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByBookerId(userId, now, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findAllByOwnerId(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwnerId(userId, now, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByOwnerId(userId, now, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByOwnerId(userId, now, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}