package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * File Name: BookingRepositoryTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   9:42 PM (UTC+3)
 * Description:
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final TestEntityManager em;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User savedOwner;
    private User savedBooker;
    private Item savedItem;
    private Booking savedPastBooking;
    private Booking savedCurrentBooking;
    private Booking savedFutureBooking;

    @BeforeEach
        // заполняем репозиторий данными (savedOwner, savedBooker, savedItem,
        // savedPastBooking, savedCurrentBooking, savedFutureBooking)
    void beforeEach() {
        User owner = new User();
        owner.setEmail("ru.practicum.shareit.user@email.ru");
        owner.setName("ru.practicum.shareit.user");
        savedOwner = userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@email.ru");
        booker.setName("booker");
        savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setOwner(savedOwner);
        item.setName("ru.practicum.shareit.item");
        item.setDescription("item_description");
        item.setAvailable(true);
        savedItem = itemRepository.save(item);

        Booking pastBooking = new Booking();
        pastBooking.setItem(savedItem);
        pastBooking.setBooker(savedBooker);
        pastBooking.setStart(LocalDateTime.now().minusMinutes(10));
        pastBooking.setEnd(LocalDateTime.now().minusMinutes(5));
        pastBooking.setStatus(BookingStatus.WAITING);
        savedPastBooking = bookingRepository.save(pastBooking);

        Booking currentBooking = new Booking();
        currentBooking.setItem(savedItem);
        currentBooking.setBooker(savedBooker);
        currentBooking.setStart(LocalDateTime.now().minusMinutes(10));
        currentBooking.setEnd(LocalDateTime.now().plusMinutes(10));
        currentBooking.setStatus(BookingStatus.WAITING);
        savedCurrentBooking = bookingRepository.save(currentBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(savedItem);
        futureBooking.setBooker(savedBooker);
        futureBooking.setStart(LocalDateTime.now().plusMinutes(5));
        futureBooking.setEnd(LocalDateTime.now().plusMinutes(10));
        futureBooking.setStatus(BookingStatus.WAITING);
        savedFutureBooking = bookingRepository.save(futureBooking);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findByBookerId_BookingsListProvidedWhenRequestedByBookerId() {
        List<Booking> foundBookings = bookingRepository.findByBookerId(savedBooker.getId());

        assertThat(foundBookings.size(), is(3));
        assertTrue(foundBookings.contains(savedPastBooking));
        assertTrue(foundBookings.contains(savedCurrentBooking));
        assertTrue(foundBookings.contains(savedFutureBooking));
    }

    @Test
    void findByItemId_BookingsListProvidedWhenRequestedByItemId() {
        List<Booking> foundBookings = bookingRepository.findByItemId(savedItem.getId());

        assertThat(foundBookings.size(), is(3));
        assertTrue(foundBookings.contains(savedPastBooking));
        assertTrue(foundBookings.contains(savedCurrentBooking));
        assertTrue(foundBookings.contains(savedFutureBooking));
    }

    @Test
    void findByItemIdIn_BookingsListProvidedWhenRequestedByItemIdInList() {
        List<Long> itemIds = new ArrayList<>();
        itemIds.add(35L);
        itemIds.add(36L);
        itemIds.add(savedItem.getId());

        List<Booking> foundBookings = bookingRepository.findByItemIdIn(itemIds);

        assertThat(foundBookings.size(), is(3));
        assertTrue(foundBookings.contains(savedPastBooking));
        assertTrue(foundBookings.contains(savedCurrentBooking));
        assertTrue(foundBookings.contains(savedFutureBooking));
    }

    @Test
    void findByItemIdIn_BookingsListIsEmptyWhenNoItemIdInList() {
        List<Long> itemIds = new ArrayList<>();
        itemIds.add(35L);
        itemIds.add(36L);

        List<Booking> foundBookings = bookingRepository.findByItemIdIn(itemIds);
        assertThat(foundBookings.size(), is(0));
    }

    @Test
    void findBookingsByBookerId_ProvidePagesByBookerId() {

        Page<Booking> bookings = bookingRepository.findBookingsByBookerId(savedBooker.getId(),
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(bookings.getTotalPages(), equalTo(1));

        assertThat(bookings, hasItem(allOf(
                hasProperty("status", equalTo(savedPastBooking.getStatus()))
        )));
    }
}

