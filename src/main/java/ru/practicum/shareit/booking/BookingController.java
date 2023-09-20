package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Sprint add-bookings.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                 @Valid @RequestBody BookingDto booking) {

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
    public List<BookingDto> getAllBookingsByBookerIdDesc(@Valid @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                         @Valid @RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(required = false, defaultValue = "20") Integer size) {

        return service.getAllBookingsByBookerIdDesc(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByItemOwnerId(@Valid @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                        @Valid @RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestParam (required = false, defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(required = false, defaultValue = "20") @Min(1) Integer size) {

        return service.getAllBookingsByItemOwnerId(ownerId, state, from, size);
    }

}
