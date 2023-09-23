package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findBookingsByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdIn(List<Long> itemsId);

    Page<Booking> findBookingsByItemIdIn(List<Long> itemsId, Pageable pageable);
}
