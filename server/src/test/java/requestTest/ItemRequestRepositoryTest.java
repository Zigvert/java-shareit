package requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.storage.CommentRepository; // Добавьте этот импорт

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItApp.class)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository; // Инъекция CommentRepository

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Очистка таблиц в правильном порядке
        bookingRepository.deleteAll(); // Сначала bookings (зависит от items и users)
        commentRepository.deleteAll(); // Затем comments (зависит от items и users)
        itemRequestRepository.deleteAll(); // Затем item_requests (зависит от users)
        itemRepository.deleteAll(); // Затем items (зависит от users)
        userRepository.deleteAll(); // Наконец, users

        user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        user2 = userRepository.save(user2);

        ItemRequest request1 = new ItemRequest();
        request1.setRequester(user1);
        request1.setDescription("Request 1");
        request1.setCreated(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setRequester(user2);
        request2.setDescription("Request 2");
        request2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request2);
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_returnsOnlyUserRequestsOrdered() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user1.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getRequester().getId()).isEqualTo(user1.getId());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDesc_returnsOtherUsersRequestsOrdered() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(user1.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getRequester().getId()).isNotEqualTo(user1.getId());
    }
}