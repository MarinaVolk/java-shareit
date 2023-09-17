package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    private User newOwner;
    private User newBooker;
    private Item newItem;
    private Booking newPastBooking;
    private Booking newCurrentBooking;
    private Booking newFutureBooking;

    @BeforeEach
    void beforeEach() {
        User owner = new User();
        owner.setEmail("user@email.ru");
        owner.setName("user");

        Assertions.assertNull(owner.getId());
        newOwner = userRepository.save(owner);
        Assertions.assertNotNull(newOwner.getId());

        User booker = new User();
        booker.setEmail("booker@email.ru");
        booker.setName("booker");

        Assertions.assertNull(booker.getId());
        newBooker = userRepository.save(booker);
        Assertions.assertNotNull(newBooker.getId());

        Item item = new Item();
        item.setOwner(newOwner);
        item.setName("item");
        item.setDescription("item_description");
        item.setAvailable(true);

        Assertions.assertNull(item.getId());
        newItem = itemRepository.save(item);
        Assertions.assertNotNull(newItem.getId());

        Booking pastBooking = new Booking();
        pastBooking.setItem(newItem);
        pastBooking.setBooker(newBooker);
        pastBooking.setStart(LocalDateTime.now().minusMinutes(10));
        pastBooking.setEnd(LocalDateTime.now().minusMinutes(5));
        pastBooking.setStatus(BookingStatus.WAITING);

        Assertions.assertNull(pastBooking.getId());
        newPastBooking = bookingRepository.save(pastBooking);
        Assertions.assertNotNull(newPastBooking.getId());

        Booking currentBooking = new Booking();
        currentBooking.setItem(newItem);
        currentBooking.setBooker(newBooker);
        currentBooking.setStart(LocalDateTime.now().minusMinutes(10));
        currentBooking.setEnd(LocalDateTime.now().plusMinutes(10));
        currentBooking.setStatus(BookingStatus.WAITING);

        Assertions.assertNull(currentBooking.getId());
        newCurrentBooking = bookingRepository.save(currentBooking);
        Assertions.assertNotNull(newCurrentBooking.getId());

        Booking futureBooking = new Booking();
        futureBooking.setItem(newItem);
        futureBooking.setBooker(newBooker);
        futureBooking.setStart(LocalDateTime.now().plusMinutes(5));
        futureBooking.setEnd(LocalDateTime.now().plusMinutes(10));
        futureBooking.setStatus(BookingStatus.WAITING);

        Assertions.assertNull(futureBooking.getId());
        newFutureBooking = bookingRepository.save(futureBooking);
        Assertions.assertNotNull(newFutureBooking.getId());
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyRepository() {
        TypedQuery<Booking> query = em.getEntityManager().createQuery(
                "select book from Booking book where book.id = :id",
                Booking.class);
        Booking foundBooking = query.setParameter("id", newFutureBooking.getId()).getSingleResult();

        assertThat(foundBooking, allOf(
                hasProperty("id", equalTo(newFutureBooking.getId())),
                hasProperty("start", equalTo(newFutureBooking.getStart())),
                hasProperty("end", equalTo(newFutureBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        ));

        assertThat(foundBooking.getItem().getId(), equalTo(newItem.getId()));
        assertThat(foundBooking.getBooker().getId(), equalTo(newBooker.getId()));
    }

}

