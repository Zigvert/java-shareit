package request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.dto.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeAndDeserialize() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Описание запроса");

        String json = objectMapper.writeValueAsString(dto);
        ItemRequestCreateDto result = objectMapper.readValue(json, ItemRequestCreateDto.class);

        assertThat(result.getDescription()).isEqualTo("Описание запроса");
    }
}
