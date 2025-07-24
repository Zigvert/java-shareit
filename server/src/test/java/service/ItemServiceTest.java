package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createItem_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("ItemName");
        createDto.setDescription("Description");
        createDto.setAvailable(true);

        Item item = ItemMapper.toItem(createDto, user);
        item.setId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(userId, createDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(createDto.getName(), result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_UserNotFound_Throws() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ItemCreateDto createDto = new ItemCreateDto();

        assertThrows(NotFoundException.class, () -> itemService.create(userId, createDto));
    }

    @Test
    void updateItem_Success() {
        Long userId = 1L;
        Long itemId = 2L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setName("OldName");
        item.setDescription("OldDesc");
        item.setAvailable(false);

        ItemDto updateDto = new ItemDto();
        updateDto.setId(itemId);
        updateDto.setName("NewName");
        updateDto.setDescription("NewDesc");
        updateDto.setAvailable(true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.update(userId, updateDto);

        assertEquals("NewName", result.getName());
        assertEquals("NewDesc", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_NotOwner_ThrowsForbidden() {
        Long userId = 1L;
        Long itemId = 2L;

        User owner = new User();
        owner.setId(999L); // другой владелец

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.update(userId, updateDto));
    }

    @Test
    void getById_Owner_SetsBookings() {
        Long userId = 1L;
        Long itemId = 2L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking lastBooking = new Booking();
        lastBooking.setId(10L);
        lastBooking.setBooker(new User());
        lastBooking.getBooker().setId(100L);

        Booking nextBooking = new Booking();
        nextBooking.setId(20L);
        nextBooking.setBooker(new User());
        nextBooking.getBooker().setId(200L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBooking(eq(itemId), any(LocalDateTime.class))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findNextBooking(eq(itemId), any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));
        when(commentRepository.findByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemDto dto = itemService.getById(userId, itemId);

        assertNotNull(dto.getLastBooking());
        assertEquals(lastBooking.getId(), dto.getLastBooking().getId());
        assertNotNull(dto.getNextBooking());
        assertEquals(nextBooking.getId(), dto.getNextBooking().getId());
    }

    @Test
    void getAllByOwner_ReturnsPagedList() {
        Long userId = 1L;
        Item item = new Item();
        item.setId(2L);
        item.setOwner(new User());
        item.getOwner().setId(userId);

        PageRequest page = PageRequest.of(0, 10);
        List<Item> items = List.of(item);

        when(itemRepository.findByOwnerIdOrderById(userId, page)).thenReturn(items);
        // мок на getById внутри getAllByOwner
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBooking(anyLong(), any())).thenReturn(Optional.empty());
        when(bookingRepository.findNextBooking(anyLong(), any())).thenReturn(Optional.empty());
        when(commentRepository.findByItemId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getAllByOwner(userId, 0, 10);

        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
    }

    @Test
    void search_EmptyText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.search("", 0, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_ValidText_ReturnsList() {
        // Arrange
        User owner = new User();
        owner.setId(1L);
        owner.setName("John");
        owner.setEmail("john@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner); // Обязательно

        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));

        // Act
        List<ItemDto> result = itemService.search("drill", 0, 10); // from, size

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .extracting(ItemDto::getName)
                .containsExactly("Drill");
    }

    @Test
    void createComment_Success() {
        Long userId = 1L;
        Long itemId = 2L;

        User user = new User();
        user.setId(userId);
        user.setName("AuthorName");

        Item item = new Item();
        item.setId(itemId);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Nice item!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);

        Comment savedComment = new Comment();
        savedComment.setId(5L);
        savedComment.setText(commentDto.getText());
        savedComment.setAuthor(user);
        savedComment.setCreated(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.createComment(userId, itemId, commentDto);

        assertEquals(commentDto.getText(), result.getText());
        assertEquals(user.getName(), result.getAuthorName());
        assertNotNull(result.getCreated());
    }

    @Test
    void createComment_NoBooking_Throws() {
        Long userId = 1L;
        Long itemId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Nice!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.createComment(userId, itemId, commentDto));
    }
}
