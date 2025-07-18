package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long userId);

    // для пагинации (если надо будет позже):
    // Page<ItemRequest> findAllByRequesterIdNot(Long userId, Pageable pageable);
}
