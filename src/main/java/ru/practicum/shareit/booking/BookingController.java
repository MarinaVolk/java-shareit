package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Sprint add-bookings.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                 @RequestBody BookingDto booking) {

        return service.addBooking(booking, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId, @RequestParam Boolean approved) {

        return service.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                     @PathVariable Long bookingId) {

        return service.getBookingByIdAndBookerId(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBookerIdDesc(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                         @RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(required = false, defaultValue = "20") Integer size) {

        return service.getAllBookingsByBookerIdDesc(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByItemOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                                        @RequestParam(required = false, defaultValue = "20") Integer size) {

        return service.getAllBookingsByItemOwnerId(ownerId, state, from, size);
    }

}
