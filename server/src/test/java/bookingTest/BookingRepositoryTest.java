package bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.item.storage.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItApp.class)
@Transactional
@Rollback
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        // Очистка всех связанных таблиц в правильном порядке
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Создание и сохранение владельца
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        // Создание и сохранение бронирующего
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        // Создание и сохранение предмета
        item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        // Создание бронирования
        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void testSaveAndFindBooking() {
        // Сохранение бронирования
        Booking saved = bookingRepository.save(booking);
        assertNotNull(saved, "Сохраненное бронирование не должно быть null");
        assertNotNull(saved.getId(), "ID сохраненного бронирования не должен быть null");

        // Проверка поиска по ID
        Optional<Booking> found = bookingRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "Бронирование должно быть найдено");
        assertEquals(saved.getId(), found.get().getId(), "ID бронирования должен совпадать");
        assertEquals(booking.getBooker().getId(), found.get().getBooker().getId(), "ID бронирующего должен совпадать");
        assertEquals(booking.getItem().getId(), found.get().getItem().getId(), "ID предмета должен совпадать");
        assertEquals(booking.getStatus(), found.get().getStatus(), "Статус бронирования должен совпадать");

        // Проверка findByBookerIdOrderByStartDesc
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty(), "Список бронирований не должен быть пустым");
        assertEquals(1, bookings.size(), "Должен быть ровно один бронирование");
        assertEquals(saved.getId(), bookings.get(0).getId(), "ID найденного бронирования должен совпадать");
    }
}