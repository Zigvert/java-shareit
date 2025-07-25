package requestTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toDto_shouldMapItemRequestToResponseDto() {
        // Подготовка исходных данных
        ItemRequest request = new ItemRequest();
        request.setId(100L);
        request.setDescription("Need a drill");
        request.setCreated(LocalDateTime.of(2025, 7, 25, 12, 0));

        ItemShortDto item1 = new ItemShortDto(1L, "Drill");
        ItemShortDto item2 = new ItemShortDto(2L, "Hammer");
        List<ItemShortDto> items = List.of(item1, item2);

        // Вызов маппера
        ItemRequestResponseDto dto = ItemRequestMapper.toDto(request, items);

        // Проверки
        assertNotNull(dto);
        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
        assertNotNull(dto.getItems());
        assertEquals(2, dto.getItems().size());
        assertEquals("Drill", dto.getItems().get(0).getName());
        assertEquals("Hammer", dto.getItems().get(1).getName());
    }
}
