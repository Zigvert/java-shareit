package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Comment comment;
    private CommentDto commentDto;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        commentDto = new CommentDto();
        commentDto.setText("Nice item");

        comment = new Comment();
        comment.setId(1L);
        comment.setText(commentDto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now()); // ВАЖНО: добавляем дату
    }

    @Test
    void create_shouldSaveComment_whenUserAndItemExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        // Подменяем поведение save, чтобы возвращать comment с установленным created
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            saved.setId(1L);
            saved.setCreated(LocalDateTime.now());
            return saved;
        });

        CommentDto result = commentService.create(user.getId(), item.getId(), commentDto);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(commentDto.getText());
        assertThat(result.getAuthorName()).isEqualTo(user.getName());
        assertThat(result.getCreated()).isNotNull();

        verify(userRepository).findById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(user.getId(), item.getId(), commentDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id " + user.getId() + " not found");

        verify(userRepository).findById(user.getId());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowNotFoundException_whenItemNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(user.getId(), item.getId(), commentDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item with id " + item.getId() + " not found");

        verify(userRepository).findById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(commentRepository, never()).save(any());
    }
}

