package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!userDto.getEmail().contains("@")) {
                throw new ValidationException("Invalid email");
            }
            if (userRepository.existsByEmail(userDto.getEmail())
                    && !userDto.getEmail().equals(existingUser.getEmail())) {
                throw new ConflictException("Email already exists");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        return UserMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
