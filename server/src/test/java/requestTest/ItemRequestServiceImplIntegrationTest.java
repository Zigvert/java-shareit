package requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = ShareItApp.class)
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setName("Test user");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();
    }

    @Test
    void createAndGetOwnRequests_success() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужен молоток");

        ItemRequestResponseDto createdRequest = itemRequestService.create(userId, createDto);

        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest.getId()).isNotNull();
        assertThat(createdRequest.getDescription()).isEqualTo("Нужен молоток");
        assertThat(createdRequest.getItems()).isEmpty();

        List<ItemRequestResponseDto> ownRequests = itemRequestService.getOwnRequests(userId);
        assertThat(ownRequests).isNotEmpty();
        assertThat(ownRequests).extracting("id").contains(createdRequest.getId());
    }

    @Test
    void getRequestById_whenNotFound_throws() {
        assertThatThrownBy(() -> itemRequestService.getRequestById(userId, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Request with id 999 not found");
    }

    @Test
    void getAllRequests_excludesOwnRequests() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна отвертка");
        itemRequestService.create(userId, createDto);

        User other = new User();
        other.setName("Other user");
        other.setEmail("other@example.com");
        other = userRepository.save(other);

        ItemRequestCreateDto otherDto = new ItemRequestCreateDto();
        otherDto.setDescription("Нужен шуруповерт");
        itemRequestService.create(other.getId(), otherDto);

        List<ItemRequestResponseDto> allRequests = itemRequestService.getAllRequests(userId);
        assertThat(allRequests).noneMatch(r -> r.getDescription().equals("Нужна отвертка"));
        assertThat(allRequests).anyMatch(r -> r.getDescription().equals("Нужен шуруповерт"));
    }
}
