package ru.practicum.client;

import lombok.RequiredArgsConstructor;
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
    private final String serverUrl = "http://server";

    public ResponseEntity<BookingResponseDto> create(Long userId, BookingDto bookingDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<BookingDto> entity = new HttpEntity<>(bookingDto, headers);
        return restTemplate.exchange(serverUrl + "/bookings", HttpMethod.POST, entity, BookingResponseDto.class);
    }

    public ResponseEntity<BookingResponseDto> update(Long userId, Long bookingId, Boolean approved) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = serverUrl + "/bookings/" + bookingId + "?approved=" + approved;
        return restTemplate.exchange(url, HttpMethod.PATCH, entity, BookingResponseDto.class);
    }

    public ResponseEntity<BookingResponseDto> getById(Long userId, Long bookingId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = serverUrl + "/bookings/" + bookingId;
        return restTemplate.exchange(url, HttpMethod.GET, entity, BookingResponseDto.class);
    }

    public ResponseEntity<List<BookingResponseDto>> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = serverUrl + "/bookings?state=" + state + "&from=" + from + "&size=" + size;
        return restTemplate.exchange(url, HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<BookingResponseDto>>() {});
    }

    public ResponseEntity<List<BookingResponseDto>> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String url = serverUrl + "/bookings/owner?state=" + state + "&from=" + from + "&size=" + size;
        return restTemplate.exchange(url, HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<BookingResponseDto>>() {});
    }
}

