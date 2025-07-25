package bookingTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingResponseDtoTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        UserShortDto userShortDto = new UserShortDto(1L); // Только ID
        ItemShortDto itemShortDto = new ItemShortDto(2L, "Item name"); // ID + name

        BookingResponseDto responseDto = new BookingResponseDto(
                10L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED,
                userShortDto,
                itemShortDto
        );

        assertEquals(10L, responseDto.getId());
        assertEquals(BookingStatus.APPROVED, responseDto.getStatus());
        assertEquals(userShortDto, responseDto.getBooker());
        assertEquals(itemShortDto, responseDto.getItem());
        assertTrue(responseDto.getStart().isBefore(responseDto.getEnd()));
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        BookingResponseDto dto = new BookingResponseDto();

        dto.setId(5L);
        dto.setStatus(BookingStatus.REJECTED);
        dto.setStart(LocalDateTime.now().plusDays(3));
        dto.setEnd(LocalDateTime.now().plusDays(4));

        UserShortDto user = new UserShortDto(3L);
        ItemShortDto item = new ItemShortDto(4L, "Some item");

        dto.setBooker(user);
        dto.setItem(item);

        assertEquals(5L, dto.getId());
        assertEquals(BookingStatus.REJECTED, dto.getStatus());
        assertEquals(user, dto.getBooker());
        assertEquals(item, dto.getItem());
    }
}
