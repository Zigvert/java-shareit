package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.practicum.dto.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestCreateDtoJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeCorrectly() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужен молоток");

        String json = objectMapper.writeValueAsString(dto);
        ItemRequestCreateDto deserialized = objectMapper.readValue(json, ItemRequestCreateDto.class);

        assertThat(deserialized.getDescription()).isEqualTo("Нужен молоток");
    }
}
