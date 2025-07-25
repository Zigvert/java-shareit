package requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItApp; // Импорт основного класса
import ru.practicum.shareit.request.ItemRequestControllerServer;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureMockMvc
class ItemRequestControllerServerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestCreateDto createDto;
    private ItemRequestResponseDto responseDto;

    @BeforeEach
    void setUp() {
        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("Need a drill");
        responseDto.setCreated(LocalDateTime.now());
        responseDto.setItems(Collections.emptyList());
    }

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        Mockito.when(itemRequestService.create(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }

    @Test
    void getOwnRequests_shouldReturnListOfRequests() throws Exception {
        Mockito.when(itemRequestService.getOwnRequests(1L))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(responseDto.getDescription())));
    }

    @Test
    void getAllRequests_shouldReturnListOfRequests() throws Exception {
        Mockito.when(itemRequestService.getAllRequests(1L))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(responseDto.getDescription())));
    }

    @Test
    void getRequestById_shouldReturnSingleRequest() throws Exception {
        Mockito.when(itemRequestService.getRequestById(1L, 1L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }
}