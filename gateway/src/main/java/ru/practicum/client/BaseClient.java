package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BaseClient {

    protected final RestTemplate rest;

    @Value("${shareit-server.url}")
    protected String serverUrl;

    @Autowired
    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    // GET с классом (для одиночных объектов)
    protected <T> ResponseEntity<T> get(String path, Long userId, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return rest.exchange(serverUrl + path, HttpMethod.GET, entity, responseType);
    }

    // GET с ParameterizedTypeReference (для списков и обобщенных типов)
    protected <T> ResponseEntity<T> get(String path, Long userId, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return rest.exchange(serverUrl + path, HttpMethod.GET, entity, responseType);
    }

    // POST
    protected <T> ResponseEntity<T> post(String path, Long userId, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return rest.exchange(serverUrl + path, HttpMethod.POST, entity, responseType);
    }

    // PUT
    protected <T> ResponseEntity<T> put(String path, Long userId, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return rest.exchange(serverUrl + path, HttpMethod.PUT, entity, responseType);
    }

    // DELETE
    protected ResponseEntity<Void> delete(String path, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return rest.exchange(serverUrl + path, HttpMethod.DELETE, entity, Void.class);
    }
}
