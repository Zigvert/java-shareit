package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    void createRequest_returnsCreatedRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужен гаечный ключ");

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужен гаечный ключ");
        responseDto.setCreated(LocalDateTime.now());
        responseDto.setItems(List.of());

        Mockito.when(itemRequestService.create(Mockito.anyLong(), Mockito.any()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужен гаечный ключ"));
    }

    @Test
    void getOwnRequests_returnsList() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужен молоток");
        responseDto.setCreated(LocalDateTime.now());
        responseDto.setItems(List.of());

        Mockito.when(itemRequestService.getOwnRequests(Mockito.anyLong()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужен молоток"));
    }
}
