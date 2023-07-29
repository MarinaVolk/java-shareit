package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemDtoForGet;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto booking, Long bookerId);

    BookingDto getBookingById(Long bookingId);

    BookingDto getBookingByIdAndBookerId(Long bookingId, Long bookerId);

    BookingDto updateBookingStatus(BookingDto bookingDto);

    List<BookingDto> getBookingsByBookerId(Long bookerId);

    List<BookingDto> getBookingsByIdItemsList(List<Long> itemsId);

    List<BookingDto> getBookingsByItemId(Long itemId);

    BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved);

    List<BookingDto> getAllBookingsByBookerIdDesc(Long bookerId, String status);

    List<BookingDto> getAllBookingsByItemOwnerId(Long ownerId, String status);

    ItemDtoForGet setLastAndNextBooking(ItemDtoForGet itemDtoForGet);

    Boolean bookingExists(Long bookingId);
}
