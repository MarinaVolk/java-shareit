package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    Booking findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findByItemIdIn(List<Long> itemsId);
}
