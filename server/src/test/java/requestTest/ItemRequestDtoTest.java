package requestTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testItemRequestCreateDtoGettersAndSetters() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");
        assertEquals("Need a drill", dto.getDescription());
    }

    @Test
    void testItemRequestCreateDtoValidation_NotBlank() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();

        // description is null (violates @NotBlank)
        dto.setDescription(null);
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        // description is blank string
        dto.setDescription(" ");
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        // valid description
        dto.setDescription("Hammer");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testItemRequestResponseDtoGettersAndSetters() {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("Need a screwdriver");
        responseDto.setCreated(LocalDateTime.now());

        ItemShortDto item1 = new ItemShortDto(10L, "Screwdriver");
        ItemShortDto item2 = new ItemShortDto(11L, "Drill");
        responseDto.setItems(List.of(item1, item2));

        assertEquals(1L, responseDto.getId());
        assertEquals("Need a screwdriver", responseDto.getDescription());
        assertNotNull(responseDto.getCreated());
        assertNotNull(responseDto.getItems());
        assertEquals(2, responseDto.getItems().size());
        assertEquals("Drill", responseDto.getItems().get(1).getName());
    }
}
