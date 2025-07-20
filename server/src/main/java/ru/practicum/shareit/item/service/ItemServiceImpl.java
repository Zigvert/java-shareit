package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
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

    @Override
    public ItemDto create(Long userId, ItemCreateDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("User is not the owner");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDto dto = ItemMapper.toDto(item);

        if (item.getOwner().getId().equals(userId)) {
            bookingRepository.findLastBooking(itemId, LocalDateTime.now())
                    .ifPresent(b -> dto.setLastBooking(new ItemDto.BookingDto(b.getId(), b.getBooker().getId())));
            bookingRepository.findNextBooking(itemId, LocalDateTime.now())
                    .ifPresent(b -> dto.setNextBooking(new ItemDto.BookingDto(b.getId(), b.getBooker().getId())));
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(c -> new CommentDto(c.getId(), c.getText(), c.getAuthor().getName(), c.getCreated().toString()))
                .collect(Collectors.toList());
        dto.setComments(comments);

        return dto;
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return itemRepository.findByOwnerIdOrderById(userId, page).stream()
                .map(this::getByIdWrapper)
                .collect(Collectors.toList());
    }

    private ItemDto getByIdWrapper(Item item) {
        return getById(item.getOwner().getId(), item.getId());
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) return List.of();
        PageRequest page = PageRequest.of(from / size, size);
        return itemRepository.search(text, page).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        boolean hasBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasBooking) {
            throw new BadRequestException("User has no valid booking to comment");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated().toString());
    }
}