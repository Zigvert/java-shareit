package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemCreateDto;
import ru.practicum.dto.ItemDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemClient {

    private final RestTemplate restTemplate;
    private final String serverUrl = "http://server";

    public ResponseEntity<ItemDto> create(Long userId, ItemCreateDto itemCreateDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemCreateDto> entity = new HttpEntity<>(itemCreateDto, headers);
        return restTemplate.exchange(serverUrl + "/items", HttpMethod.POST, entity, ItemDto.class);
    }

    public ResponseEntity<ItemDto> update(Long userId, Long itemId, ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemDto> entity = new HttpEntity<>(itemDto, headers);
        return restTemplate.exchange(serverUrl + "/items/" + itemId, HttpMethod.PATCH, entity, ItemDto.class);
    }

    public ResponseEntity<ItemDto> getById(Long userId, Long itemId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl + "/items/" + itemId, HttpMethod.GET, entity, ItemDto.class);
    }

    public ResponseEntity<List<ItemDto>> getAllByOwner(Long userId, Integer from, Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = serverUrl + "/items?from=" + from + "&size=" + size;
        return restTemplate.exchange(url, HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<ItemDto>>() {});
    }

    public ResponseEntity<List<ItemDto>> search(String text, Integer from, Integer size) {
        String url = serverUrl + "/items/search?text=" + text + "&from=" + from + "&size=" + size;
        return restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ItemDto>>() {});
    }

    public ResponseEntity<CommentDto> createComment(Long userId, Long itemId, CommentDto commentDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<CommentDto> entity = new HttpEntity<>(commentDto, headers);
        return restTemplate.exchange(serverUrl + "/items/" + itemId + "/comment", HttpMethod.POST, entity, CommentDto.class);
    }
}
