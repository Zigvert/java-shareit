package userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void create_success() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("john@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(dto.getName());
        savedUser.setEmail(dto.getEmail());

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@example.com");

        verify(userRepository).existsByEmail(dto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_shouldThrowValidationException_whenEmailInvalid() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("invalidemail");

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid email");

        verify(userRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowConflictException_whenEmailExists() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("john@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository).existsByEmail(dto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_success_updateNameAndEmail() {
        Long id = 1L;
        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(id, updateDto);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@example.com");

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail(updateDto.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void update_shouldThrowNotFoundException_whenUserNotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");

        assertThatThrownBy(() -> userService.update(id, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowValidationException_whenEmailInvalid() {
        Long id = 1L;
        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        UserDto updateDto = new UserDto();
        updateDto.setEmail("invalidemail");

        assertThatThrownBy(() -> userService.update(id, updateDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid email");

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowConflictException_whenEmailExistsForAnotherUser() {
        Long id = 1L;
        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@example.com");

        assertThatThrownBy(() -> userService.update(id, updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto dto = userService.getById(id);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("John");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");

        verify(userRepository).findById(id);
    }

    @Test
    void getById_shouldThrowNotFoundException_whenUserNotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(id);
    }

    @Test
    void getAll_success() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").containsExactly(1L, 2L);

        verify(userRepository).findAll();
    }

    @Test
    void delete_success() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        userService.delete(id);

        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenUserNotFound() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).existsById(id);
        verify(userRepository, never()).deleteById(any());
    }
}
