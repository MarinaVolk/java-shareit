package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdIn(List<Long> itemsId);
}
