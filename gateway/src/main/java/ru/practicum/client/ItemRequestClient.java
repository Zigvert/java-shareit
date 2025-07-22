package ru.practicum.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.ItemRequestCreateDto;
import ru.practicum.shareit.dto.ItemRequestResponseDto;

import java.util.List;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String REQUESTS_API = "/requests";

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<ItemRequestResponseDto> createRequest(Long userId, ItemRequestCreateDto dto) {
        return post(REQUESTS_API, userId, dto, ItemRequestResponseDto.class);
    }

    public ResponseEntity<List<ItemRequestResponseDto>> getOwnRequests(Long userId) {
        return get(REQUESTS_API, userId, new ParameterizedTypeReference<List<ItemRequestResponseDto>>() {});
    }

    public ResponseEntity<List<ItemRequestResponseDto>> getAllRequests(Long userId) {
        return get(REQUESTS_API + "/all", userId, new ParameterizedTypeReference<List<ItemRequestResponseDto>>() {});
    }

    public ResponseEntity<ItemRequestResponseDto> getRequestById(Long userId, Long requestId) {
        return get(REQUESTS_API + "/" + requestId, userId, ItemRequestResponseDto.class);
    }
}
