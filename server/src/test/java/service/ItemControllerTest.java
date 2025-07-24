package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void createItem() throws Exception {
        Long userId = 1L;
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("TestItem");
        createDto.setDescription("Desc");
        createDto.setAvailable(true);
        createDto.setRequestId(null);  // Можно null, если не используется

        // Создаем ItemDto, который вернет мок сервиса
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName(createDto.getName());
        itemDto.setDescription(createDto.getDescription());
        itemDto.setAvailable(createDto.getAvailable());
        itemDto.setOwnerId(userId);
        itemDto.setRequestId(null);
        itemDto.setComments(List.of()); // пустой список комментариев
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);

        when(itemService.create(eq(userId), any(ItemCreateDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void updateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        ItemDto updateDto = new ItemDto();
        updateDto.setName("UpdatedName");

        ItemDto returnedDto = new ItemDto();
        returnedDto.setId(itemId);
        returnedDto.setName("UpdatedName");

        when(itemService.update(eq(userId), any(ItemDto.class))).thenReturn(returnedDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("UpdatedName"));
    }

    @Test
    void getById() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("ItemName");

        when(itemService.getById(userId, itemId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("ItemName"));
    }

    @Test
    void getAllByOwner() throws Exception {
        Long userId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item1");

        when(itemService.getAllByOwner(userId, 0, 10)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void search() throws Exception {
        String text = "search";

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item1");

        when(itemService.search(text, 0, 10)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void createComment() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        CommentDto commentDto = new CommentDto();
        commentDto.setId(5L);
        commentDto.setText("Nice item");
        commentDto.setAuthorName("Author");
        commentDto.setCreated("2025-07-24T00:00:00");

        when(itemService.createComment(eq(userId), eq(itemId), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated()));
    }
}
