package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.UserDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;
    private final String serverUrl = "http://server";

    public ResponseEntity<UserDto> create(UserDto userDto) {
        HttpEntity<UserDto> entity = new HttpEntity<>(userDto);
        return restTemplate.exchange(serverUrl + "/users", HttpMethod.POST, entity, UserDto.class);
    }

    public ResponseEntity<UserDto> update(Long id, UserDto userDto) {
        HttpEntity<UserDto> entity = new HttpEntity<>(userDto);
        return restTemplate.exchange(serverUrl + "/users/" + id, HttpMethod.PATCH, entity, UserDto.class);
    }

    public ResponseEntity<UserDto> getById(Long id) {
        return restTemplate.exchange(serverUrl + "/users/" + id, HttpMethod.GET, null, UserDto.class);
    }

    public ResponseEntity<List<UserDto>> getAll() {
        return restTemplate.exchange(serverUrl + "/users", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UserDto>>() {});
    }

    public ResponseEntity<Void> delete(Long id) {
        return restTemplate.exchange(serverUrl + "/users/" + id, HttpMethod.DELETE, null, Void.class);
    }
}
