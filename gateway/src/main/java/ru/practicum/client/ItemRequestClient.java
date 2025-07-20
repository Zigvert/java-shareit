package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestResponseDto;

import java.util.List;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String REQUESTS_API = "/requests";

    @Autowired
    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<ItemRequestResponseDto> createRequest(Long userId, ItemRequestCreateDto dto) {
        return post(REQUESTS_API, userId, dto, ItemRequestResponseDto.class);
    }

    public ResponseEntity<List> getOwnRequests(Long userId) {
        return get(REQUESTS_API, userId, List.class);
    }

    public ResponseEntity<List> getAllRequests(Long userId) {
        return get(REQUESTS_API + "/all", userId, List.class);
    }

    public ResponseEntity<ItemRequestResponseDto> getRequestById(Long userId, Long requestId) {
        return get(REQUESTS_API + "/" + requestId, userId, ItemRequestResponseDto.class);
    }
}
