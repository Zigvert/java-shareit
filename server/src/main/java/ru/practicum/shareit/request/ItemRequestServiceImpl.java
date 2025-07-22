package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.dto.ItemRequestCreateDto;
import ru.practicum.shareit.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final Logger log = LoggerFactory.getLogger(ItemRequestServiceImpl.class);

    @Override
    @Transactional
    public ItemRequestResponseDto create(Long userId, ItemRequestCreateDto dto) {
        User user = getUserOrThrow(userId);

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description must not be empty");
        }

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = requestRepository.save(request);
        log.info("Created new ItemRequest with id {}", savedRequest.getId());
        return ItemRequestMapper.toDto(savedRequest, List.of());
    }

    @Override
    public List<ItemRequestResponseDto> getOwnRequests(Long userId) {
        getUserOrThrow(userId);

        return requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        getItemsByRequestId(request.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId) {
        getUserOrThrow(userId);

        return requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId).stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        getItemsByRequestId(request.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        getUserOrThrow(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));

        return ItemRequestMapper.toDto(request, getItemsByRequestId(request.getId()));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private List<ItemShortDto> getItemsByRequestId(Long requestId) {
        return itemRepository.findAll().stream()
                .filter(item -> requestId.equals(item.getRequestId()))
                .map(item -> new ItemShortDto(item.getId(), item.getName()))
                .collect(Collectors.toList());
    }
}
