package ru.practicum.shareit.user;  // Поставь здесь свой пакет, который является подпакетом ru.practicum.shareit

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        UserDto savedUserDto = new UserDto();
        savedUserDto.setId(1L);
        savedUserDto.setName("John Doe");
        savedUserDto.setEmail("john@example.com");

        Mockito.when(userService.create(Mockito.any(UserDto.class))).thenReturn(savedUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(savedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(savedUserDto.getEmail()));
    }

    @Test
    void updateUser_success() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setName("Jane Doe");
        updateDto.setEmail("jane@example.com");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setName("Jane Doe");
        updatedUserDto.setEmail("jane@example.com");

        Mockito.when(userService.update(Mockito.eq(1L), Mockito.any(UserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(updatedUserDto.getEmail()));
    }

    @Test
    void getUserById_success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        Mockito.when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getAllUsers_success() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john@example.com");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("Jane Roe");
        user2.setEmail("jane@example.com");

        List<UserDto> users = List.of(user1, user2);

        Mockito.when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Roe"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
    }

    @Test
    void deleteUser_success() throws Exception {
        Mockito.doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void handleIllegalArgumentException_returnsConflict() throws Exception {
        String errorMessage = "User already exists";

        Mockito.when(userService.create(Mockito.any(UserDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }
}
