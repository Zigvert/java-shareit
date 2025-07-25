package bookingTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toResponseDto_shouldMapBookingToBookingResponseDto() {
        // given
        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        Item item = new Item();
        item.setId(2L);
        item.setName("Item Name");

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        // when
        BookingResponseDto response = BookingMapper.toResponseDto(booking);

        // then
        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
        assertEquals(booking.getStart(), response.getStart());
        assertEquals(booking.getEnd(), response.getEnd());
        assertEquals(booking.getStatus(), response.getStatus());
        assertEquals(booking.getBooker().getId(), response.getBooker().getId());
        assertEquals(booking.getItem().getId(), response.getItem().getId());
        assertEquals(booking.getItem().getName(), response.getItem().getName());
    }

    @Test
    void fromDto_shouldMapDtoToBooking() {
        // given
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(2L);

        // when
        Booking booking = BookingMapper.fromDto(dto, booker, item);

        // then
        assertNotNull(booking);
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(booker, booking.getBooker());
        assertEquals(item, booking.getItem());
    }
}
