package userTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUser_shouldMapDtoToUser() {
        UserDto dto = new UserDto();
        dto.setId(10L);
        dto.setName("John Doe");
        dto.setEmail("john@example.com");

        User user = UserMapper.toUser(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(dto.getId());
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void toUser_shouldReturnNull_whenDtoIsNull() {
        User user = UserMapper.toUser(null);
        assertThat(user).isNull();
    }

    @Test
    void toDto_shouldMapUserToDto() {
        User user = new User();
        user.setId(20L);
        user.setName("Jane Roe");
        user.setEmail("jane@example.com");

        UserDto dto = UserMapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void toDto_shouldReturnNull_whenUserIsNull() {
        UserDto dto = UserMapper.toDto(null);
        assertThat(dto).isNull();
    }
}
