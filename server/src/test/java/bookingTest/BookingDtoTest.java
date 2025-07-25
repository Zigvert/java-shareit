package bookingTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;

import jakarta.validation.*;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoTest {

    private final Validator validator;

    public BookingDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setItemId(2L);
        dto.setBookerId(3L);
        dto.setStatus(null);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getItemId());
        assertEquals(3L, dto.getBookerId());
        assertNull(dto.getStatus());
        assertTrue(dto.getStart().isAfter(LocalDateTime.now()));
        assertTrue(dto.getEnd().isAfter(dto.getStart()));
    }

    @Test
    void shouldFailValidation_whenStartIsNull() {
        BookingDto dto = new BookingDto();
        dto.setStart(null);
        dto.setEnd(LocalDateTime.now().plusDays(1));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("start")));
    }

    @Test
    void shouldFailValidation_whenEndIsNull() {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(null);
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("end")));
    }

    @Test
    void shouldFailValidation_whenItemIdIsNull() {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItemId(null);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("itemId")));
    }

    @Test
    void shouldFailValidation_whenStartOrEndIsInPast() {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now().minusDays(1));
        dto.setEnd(LocalDateTime.now().minusDays(1));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("start")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("end")));
    }
}
