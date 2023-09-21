package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid BookItemRequestDto bookItemRequestDto) {
        return bookingClient.bookItem(userId, bookItemRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam Boolean approved) {

        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {

        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingStatus state = BookingStatus.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        return bookingClient.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByItemOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingStatus state = BookingStatus.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        return bookingClient.getBookingByItemOwnerId(userId, state, from, size);
    }
}
