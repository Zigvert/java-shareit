package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.BookingResponseDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingClient {

    private final RestTemplate restTemplate;

    @Value("${server.url}")
    private String serverUrl;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private HttpEntity<?> createEntity(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, userId.toString());
        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> createEntity(Long userId, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_HEADER, userId.toString());
        return new HttpEntity<>(body, headers);
    }

    public ResponseEntity<BookingResponseDto> create(Long userId, BookingDto bookingDto) {
        return restTemplate.exchange(
                serverUrl + "/bookings",
                HttpMethod.POST,
                createEntity(userId, bookingDto),
                BookingResponseDto.class
        );
    }

    public ResponseEntity<BookingResponseDto> update(Long userId, Long bookingId, Boolean approved) {
        String url = String.format("%s/bookings/%d?approved=%b", serverUrl, bookingId, approved);
        return restTemplate.exchange(url, HttpMethod.PATCH, createEntity(userId), BookingResponseDto.class);
    }

    public ResponseEntity<BookingResponseDto> getById(Long userId, Long bookingId) {
        String url = serverUrl + "/bookings/" + bookingId;
        return restTemplate.exchange(url, HttpMethod.GET, createEntity(userId), BookingResponseDto.class);
    }

    public ResponseEntity<List<BookingResponseDto>> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        String url = String.format("%s/bookings?state=%s&from=%d&size=%d", serverUrl, state, from, size);
        return restTemplate.exchange(url, HttpMethod.GET, createEntity(userId),
                new ParameterizedTypeReference<List<BookingResponseDto>>() {});
    }

    public ResponseEntity<List<BookingResponseDto>> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        String url = String.format("%s/bookings/owner?state=%s&from=%d&size=%d", serverUrl, state, from, size);
        return restTemplate.exchange(url, HttpMethod.GET, createEntity(userId),
                new ParameterizedTypeReference<List<BookingResponseDto>>() {});
    }
}
