package itemTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    void toItem_shouldMapCorrectly() {
        User owner = new User();
        owner.setId(1L);

        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Hammer");
        dto.setDescription("Heavy hammer");
        dto.setAvailable(true);
        dto.setRequestId(42L);

        Item item = ItemMapper.toItem(dto, owner);

        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
        assertEquals(dto.getRequestId(), item.getRequestId());
        assertEquals(owner, item.getOwner());
    }

    @Test
    void toDto_shouldMapCorrectly() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Hammer");
        item.setDescription("Heavy hammer");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(42L);

        ItemDto dto = ItemMapper.toDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(owner.getId(), dto.getOwnerId());
        assertEquals(item.getRequestId(), dto.getRequestId());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertNull(dto.getComments());
    }
}
