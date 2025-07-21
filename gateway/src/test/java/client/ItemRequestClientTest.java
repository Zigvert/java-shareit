package client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Через рефлексию или сеттер установим serverUrl, т.к. поле protected
        // Для простоты сделаем рефлексию:
        try {
            var field = itemRequestClient.getClass().getSuperclass().getDeclaredField("serverUrl");
            field.setAccessible(true);
            field.set(itemRequestClient, "http://localhost:8080");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createRequest_returnsResponseEntity() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Test description");

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("Test description");

        ResponseEntity<ItemRequestResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ItemRequestResponseDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<ItemRequestResponseDto> result =
                itemRequestClient.createRequest(1L, createDto);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getDescription()).isEqualTo("Test description");
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ItemRequestResponseDto.class));
    }

    @Test
    void getOwnRequests_returnsResponseEntity() {
        List<ItemRequestResponseDto> mockList = List.of(new ItemRequestResponseDto());

        ResponseEntity<List<ItemRequestResponseDto>> responseEntity =
                new ResponseEntity<>(mockList, HttpStatus.OK);

        // Важно: используем ParameterizedTypeReference в when
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<ItemRequestResponseDto>>>any()))
                .thenReturn(responseEntity);

        ResponseEntity<List<ItemRequestResponseDto>> result = itemRequestClient.getOwnRequests(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<ItemRequestResponseDto>>>any());
    }
}
