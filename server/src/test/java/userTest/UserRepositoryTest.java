package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItApp.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_shouldReturnTrueIfUserExists() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("test@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalseIfUserDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }
}
