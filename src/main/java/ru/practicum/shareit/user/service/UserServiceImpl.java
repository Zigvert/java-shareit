package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto create(UserDto userDto) {
        if (repository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new ConflictException("Email already exists: " + userDto.getEmail());
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(repository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existing = repository.findById(id);
        if (existing == null) {
            throw new NotFoundException("User not found with id: " + id);
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existing.getEmail())) {
            if (repository.findAll().stream()
                    .anyMatch(user -> user.getEmail().equals(userDto.getEmail()) && !user.getId().equals(id))) {
                throw new ConflictException("Email already exists: " + userDto.getEmail());
            }
            existing.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }

        return UserMapper.toDto(repository.save(existing));
    }

    @Override
    public UserDto getById(Long id) {
        User user = repository.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
