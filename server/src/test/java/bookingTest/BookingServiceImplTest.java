package bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private ItemRepository itemRepository;
    @InjectMocks private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booker = new User();
        booker.setId(1L);
        booker.setName("User");
        booker.setEmail("user@mail.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Cordless drill");
        item.setAvailable(true);
        item.setOwner(owner);

        bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.of(2025, 7, 26, 6, 50));
        bookingDto.setEnd(LocalDateTime.of(2025, 7, 27, 6, 50));

        booking = BookingMapper.fromDto(bookingDto, booker, item);
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void create_shouldSaveBooking_whenValid() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndStatusAndStartLessThanAndEndGreaterThan(
                anyLong(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        var result = bookingService.create(booker.getId(), bookingDto);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.create(booker.getId(), bookingDto));
    }

    @Test
    void create_shouldThrow_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), bookingDto));
    }

    @Test
    void update_shouldApprove_whenOwnerApproves() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = bookingService.update(owner.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void update_shouldThrow_whenNotOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenException.class, () -> bookingService.update(booker.getId(), booking.getId(), true));
    }

    @Test
    void update_shouldThrow_whenStatusNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.update(owner.getId(), booking.getId(), false));
    }

    @Test
    void getById_shouldReturnBooking_forBookerOrOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        var result1 = bookingService.getById(booker.getId(), booking.getId());
        assertNotNull(result1);

        var result2 = bookingService.getById(owner.getId(), booking.getId());
        assertNotNull(result2);
    }

    @Test
    void getById_shouldThrow_whenNotAuthorized() {
        User stranger = new User();
        stranger.setId(3L);
        stranger.setName("Nope");
        stranger.setEmail("no@mail.com");

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.getById(stranger.getId(), booking.getId()));
    }

    @Test
    void getAllByBooker_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(booker.getId(), "ALL", 0, 10));
    }

    @Test
    void getAllByOwner_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(owner.getId(), "ALL", 0, 10));
    }

    @Test
    void getAllByBooker_shouldThrow_whenInvalidState() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        assertThrows(ValidationException.class, () -> bookingService.getAllByBooker(booker.getId(), "BAD", 0, 10));
    }
}
