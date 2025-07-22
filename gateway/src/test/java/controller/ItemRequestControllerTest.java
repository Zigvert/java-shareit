package ru.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestResponseDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ItemRequestControllerTest {

    private ItemRequestClient itemRequestClient;
    private ItemRequestController controller;

    @BeforeEach
    void setUp() {
        itemRequestClient = Mockito.mock(ItemRequestClient.class);
        controller = new ItemRequestController(itemRequestClient);
    }

    @Test
    void shouldCreateRequest() {
        Long userId = 1L;
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Need item");

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(100L);
        responseDto.setDescription("Need item");

        ResponseEntity<ItemRequestResponseDto> mockedResponse = ResponseEntity.ok(responseDto);

        when(itemRequestClient.createRequest(userId, requestDto)).thenReturn(mockedResponse);

        ResponseEntity<?> result = controller.createRequest(userId, requestDto);

        verify(itemRequestClient, times(1)).createRequest(userId, requestDto);
        assertEquals(mockedResponse, result);
    }

    @Test
    void shouldGetOwnRequests() {
        Long userId = 2L;
        List<ItemRequestResponseDto> responseList = List.of(new ItemRequestResponseDto());
        ResponseEntity<List<ItemRequestResponseDto>> mockedResponse = ResponseEntity.ok(responseList);

        when(itemRequestClient.getOwnRequests(userId)).thenReturn(mockedResponse);

        ResponseEntity<?> result = controller.getOwnRequests(userId);

        verify(itemRequestClient, times(1)).getOwnRequests(userId);
        assertEquals(mockedResponse, result);
    }

    @Test
    void shouldGetAllRequests() {
        Long userId = 3L;
        List<ItemRequestResponseDto> responseList = List.of(new ItemRequestResponseDto());
        ResponseEntity<List<ItemRequestResponseDto>> mockedResponse = ResponseEntity.ok(responseList);

        when(itemRequestClient.getAllRequests(userId)).thenReturn(mockedResponse);

        ResponseEntity<?> result = controller.getAllRequests(userId);

        verify(itemRequestClient, times(1)).getAllRequests(userId);
        assertEquals(mockedResponse, result);
    }

    @Test
    void shouldGetRequestById() {
        Long userId = 4L;
        Long requestId = 10L;

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(requestId);

        ResponseEntity<ItemRequestResponseDto> mockedResponse = ResponseEntity.ok(responseDto);

        when(itemRequestClient.getRequestById(userId, requestId)).thenReturn(mockedResponse);

        ResponseEntity<?> result = controller.getRequestById(userId, requestId);

        verify(itemRequestClient, times(1)).getRequestById(userId, requestId);
        assertEquals(mockedResponse, result);
    }
}
