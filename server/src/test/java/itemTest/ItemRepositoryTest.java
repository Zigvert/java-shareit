package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // если хочешь использовать реальную БД (postgres), иначе можно убрать
@ActiveProfiles("test") // если используешь профиль test для H2
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setup() {
        // Сохраняем пользователей в БД через entityManager
        user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        user1 = entityManager.persistAndFlush(user1);

        user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        user2 = entityManager.persistAndFlush(user2);

        // Создаем товары
        item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Electric drill");
        item1.setAvailable(true);
        item1.setOwner(user1);

        item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);
        item2.setOwner(user1);

        item3 = new Item();
        item3.setName("Saw");
        item3.setDescription("Hand saw");
        item3.setAvailable(false);
        item3.setOwner(user2);

        itemRepository.deleteAll();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    void findByOwnerIdOrderById_shouldReturnItemsOfOwnerWithPaging() {
        List<Item> items = itemRepository.findByOwnerIdOrderById(user1.getId(), PageRequest.of(0, 10));
        assertThat(items).hasSize(2);
        assertThat(items).allMatch(item -> item.getOwner().getId().equals(user1.getId()));
        assertThat(items.get(0).getId()).isLessThan(items.get(1).getId());
    }

    @Test
    void search_shouldReturnAvailableItemsMatchingText() {
        List<Item> items = itemRepository.search("drill", PageRequest.of(0, 10));
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualToIgnoringCase("Drill");

        items = itemRepository.search("heavy", PageRequest.of(0, 10));
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualToIgnoringCase("Hammer");

        // item3 не доступен, поэтому не должен возвращаться
        items = itemRepository.search("saw", PageRequest.of(0, 10));
        assertThat(items).isEmpty();
    }
}
