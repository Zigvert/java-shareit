package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class BaseClient {

    protected final RestTemplate rest;

    @Value("${shareit-server.url}")
    protected String serverUrl;

    @Autowired
    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<T> get(String path, Long userId, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return rest.exchange(serverUrl + path, HttpMethod.GET, entity, responseType);
    }

    protected <T> ResponseEntity<T> post(String path, Long userId, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return rest.exchange(serverUrl + path, HttpMethod.POST, entity, responseType);
    }

    // Добавь другие HTTP методы (PUT, DELETE) по необходимости
}
