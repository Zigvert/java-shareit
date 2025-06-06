package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(user);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User is not the owner of the item");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto get(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
        ItemDto itemDto = itemMapper.toDto(item);

        // Добавление комментариев
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);

        // Добавление lastBooking и nextBooking для владельца
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> lastBooking = bookingRepository.findTop1ByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                    itemId, BookingStatus.APPROVED, now);
            List<Booking> nextBooking = bookingRepository.findTop1ByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, now);
            itemDto.setLastBooking(lastBooking.isEmpty() ? null : BookingMapper.toShortDto(lastBooking.get(0)));
            itemDto.setNextBooking(nextBooking.isEmpty() ? null : BookingMapper.toShortDto(nextBooking.get(0)));
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.findAllByOwnerId(userId, pageRequest).stream()
                .map(item -> {
                    ItemDto dto = itemMapper.toDto(item);
                    List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                            .map(this::toCommentDto)
                            .collect(Collectors.toList());
                    dto.setComments(comments);
                    LocalDateTime now = LocalDateTime.now();
                    List<Booking> lastBooking = bookingRepository.findTop1ByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                            item.getId(), BookingStatus.APPROVED, now);
                    List<Booking> nextBooking = bookingRepository.findTop1ByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), BookingStatus.APPROVED, now);
                    dto.setLastBooking(lastBooking.isEmpty() ? null : BookingMapper.toShortDto(lastBooking.get(0)));
                    dto.setNextBooking(nextBooking.isEmpty() ? null : BookingMapper.toShortDto(nextBooking.get(0)));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                        text, text, true, pageRequest)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        // Проверка, что пользователь завершил бронирование
        boolean hasBooking = bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(
                        userId, itemId, LocalDateTime.now()).stream()
                .anyMatch(booking -> booking.getStatus().equals(BookingStatus.APPROVED));
        if (!hasBooking) {
            throw new ValidationException("User has not completed a booking for this item");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return toCommentDto(commentRepository.save(comment));
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }
}
