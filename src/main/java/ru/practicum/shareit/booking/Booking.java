package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_item_id", columnList = "item_id"),
        @Index(name = "idx_booker_id", columnList = "booker_id")
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    @Future(message = "Start date must be in the future")
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    @Future(message = "End date must be in the future")
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}