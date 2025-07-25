package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@Rollback
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item1;
    private Item item2;

    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    void setUp() {
        // Очистка всех связанных таблиц в правильном порядке
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Создание и сохранение пользователя
        user = new User();
        user.setName("Commenter");
        user.setEmail("commenter@example.com");
        user = userRepository.save(user);

        // Создание и сохранение предметов
        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(user);
        item2 = itemRepository.save(item2);

        // Создание и сохранение комментариев
        comment1 = new Comment();
        comment1.setText("Comment for item1");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        comment2 = new Comment();
        comment2.setText("Another comment for item1");
        comment2.setItem(item1);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        comment3 = new Comment();
        comment3.setText("Comment for item2");
        comment3.setItem(item2);
        comment3.setAuthor(user);
        comment3.setCreated(LocalDateTime.now());
        commentRepository.save(comment3);
    }

    @Test
    void findByItemId_shouldReturnCommentsForSpecificItem() {
        List<Comment> commentsForItem1 = commentRepository.findByItemId(item1.getId());
        assertThat(commentsForItem1).hasSize(2);
        assertThat(commentsForItem1).allMatch(c -> c.getItem().getId().equals(item1.getId()));

        List<Comment> commentsForItem2 = commentRepository.findByItemId(item2.getId());
        assertThat(commentsForItem2).hasSize(1);
        assertThat(commentsForItem2.get(0).getItem().getId()).isEqualTo(item2.getId());
    }
}