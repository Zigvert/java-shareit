package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestResponseDto;
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

    @Override
    @Transactional
    public ItemRequestResponseDto create(Long userId, ItemRequestCreateDto dto) {
        User user = getUserOrThrow(userId);

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        request = requestRepository.save(request);
        return ItemRequestMapper.toDto(request, List.of());
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
                .orElseThrow(() -> new NotFoundException("Request not found"));

        return ItemRequestMapper.toDto(request, getItemsByRequestId(request.getId()));
    }

    // ======= Вспомогательные методы =======

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private List<ItemShortDto> getItemsByRequestId(Long requestId) {
        List<Item> items = itemRepository.findAll()
                .stream()
                .filter(item -> requestId.equals(item.getRequestId()))
                .collect(Collectors.toList());

        return items.stream()
                .map(item -> new ItemShortDto(item.getId(), item.getName()))
                .collect(Collectors.toList());
    }
}
