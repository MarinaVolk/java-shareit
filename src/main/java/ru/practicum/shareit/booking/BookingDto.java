package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

/**
 * Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private ItemForResponseDto item;

    private UserDto booker;

    private BookingStatus status;
}
