package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemForResponse item;

    private Booker booker;

    private BookingStatus status;
}
