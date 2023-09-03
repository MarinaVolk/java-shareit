package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnSupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemResponseShortDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private static UserDto createUserDto(String email, String name) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private static ItemDto createItemDto(String description, String name, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription(description);
        itemDto.setName(name);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private static BookingDto createBookingDto(BookingStatus status, Long itemId, Long bookerId,
                                               LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);
        bookingDto.setItem(new ItemResponseShortDto(1L, "item1"));
        bookingDto.setBooker(createUserDto("user1@email.ru", "user1name"));
        bookingDto.setStatus(status);
        return bookingDto;
    }

    private static ItemRequestDto createItemRequest(String description) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        itemRequestDto.setRequestorId(1L);
        itemRequestDto.setCreated(LocalDateTime.now().plusDays(1));
        return itemRequestDto;
    }

    private static CommentDto createComment(String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        return commentDto;
    }

    @Test
    void addBookingShouldCreateBooking() {

        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        ItemDto itemDto2 = createItemDto("item2Description", "item2", false);
        ItemDto itemDto3 = createItemDto("item3Description", "item3", true);

        UserDto userDto1 = createUserDto("user1@email", "user1");
        UserDto userDto2 = createUserDto("user2@email", "user2");
        UserDto userDto3 = createUserDto("user3@email", "user3");

        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);
        userDto3 = userService.addUser(userDto3);

        itemDto1 = itemService.addItem(1L, itemDto1);
        itemDto2 = itemService.addItem(1L, itemDto2);
        itemDto3 = itemService.addItem(1L, itemDto3);

        //Тесты ошибок при создании бронирования
        BookingDto bookingForItem1FromUser2 = createBookingDto(BookingStatus.APPROVED, 1L, 2L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(3));

        final NotFoundException userNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingForItem1FromUser2, 99L)
        );

        assertEquals("Пользователя с id=99 в базе нет.", userNotFoundException.getMessage());

        BookingDto bookingWithWrongItemId = createBookingDto(BookingStatus.APPROVED, 99L, 2L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        final NotFoundException itemNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingWithWrongItemId, 2L)
        );

        assertEquals("Такой вещи в базе нет.", itemNotFoundException.getMessage());

        BookingDto bookingForFalseAvailableStatus = createBookingDto(BookingStatus.APPROVED, 2L, 2L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        final ValidationException incorrectItemStatusForBookingException = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookingForFalseAvailableStatus, 2L)
        );

        assertEquals("Вещь недоступна для бронирования.", incorrectItemStatusForBookingException.getMessage());

        BookingDto bookingForItem1FromOwner = createBookingDto(BookingStatus.APPROVED, 1L, 1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        final NotFoundException bookingNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingForItem1FromOwner, 1L)
        );

        assertEquals("Владелец не может бронировать свою вещь.", bookingNotFoundException.getMessage());
    }

    @Test
    void getBookingsShouldProvideBookingsUponRequest() throws InterruptedException {

        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        ItemDto itemDto2 = createItemDto("item2Description", "item2", true);
        ItemDto itemDto3 = createItemDto("item3Description", "item3", true);

        UserDto userDto1 = createUserDto("user1@email", "user1");
        UserDto userDto2 = createUserDto("user2@email", "user2");
        UserDto userDto3 = createUserDto("user3@email", "user3");

        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);
        userDto3 = userService.addUser(userDto3);

        itemDto1 = itemService.addItem(1L, itemDto1);
        itemDto2 = itemService.addItem(1L, itemDto2);
        itemDto3 = itemService.addItem(1L, itemDto3);

        //Тест исключения при получении бронирований по неверному bookerId
        final NotFoundException userNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingsByBookerIdDesc(99L, "PAST", 0, 20)
        );

        assertEquals("Такого пользователя в базе нет.", userNotFoundException.getMessage());

        BookingDto bookingPast = createBookingDto(BookingStatus.APPROVED, 1L, 3L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(3));

        BookingDto bookingCurrent = createBookingDto(BookingStatus.APPROVED, 1L, 3L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusDays(1));

        BookingDto bookingFuture = createBookingDto(BookingStatus.APPROVED, 2L, 3L,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4));

        List<BookingDto> bookingsEmpty = bookingService.getAllBookingsByBookerIdDesc(3L, "ALL", 0, 20);

        assertEquals(0, bookingsEmpty.size());

        BookingDto bookingDtoPast = bookingService.addBooking(bookingPast, 3L);
        BookingDto bookingDtoCurrent = bookingService.addBooking(bookingCurrent, 3L);
        BookingDto bookingDtoFuture = bookingService.addBooking(bookingFuture, 3L);

        Thread.sleep(4000);

        List<BookingDto> bookingsCurrent = bookingService.getAllBookingsByBookerIdDesc(3L, "CURRENT", 0, 20);

        assertEquals(1, bookingsCurrent.size());

        List<BookingDto> bookingsPast = bookingService.getAllBookingsByBookerIdDesc(3L, "PAST", 0, 20);

        assertEquals(1, bookingsPast.size());

        List<BookingDto> bookingsFuture = bookingService.getAllBookingsByBookerIdDesc(3L, "FUTURE", 0, 20);

        assertEquals(1, bookingsFuture.size());

        // тест получения bookings

        List<BookingDto> bookingsWaiting = bookingService.getAllBookingsByBookerIdDesc(3L, "WAITING", 0, 20);

        assertEquals(3, bookingsWaiting.size());

        List<BookingDto> bookingsRejected = bookingService.getAllBookingsByBookerIdDesc(3L, "REJECTED", 0, 20);

        assertEquals(0, bookingsRejected.size());

        List<BookingDto> bookingsAll = bookingService.getAllBookingsByBookerIdDesc(3L, "ALL", 0, 20);

        assertEquals(3, bookingsAll.size());

        final UnSupportedStatusException unSupportedStatusException = assertThrows(
                UnSupportedStatusException.class,
                () -> bookingService.getAllBookingsByBookerIdDesc(3L, "WWW", 0, 20)
        );

        assertEquals("Unknown state: WWW", unSupportedStatusException.getMessage());

        // тест метода getAllBookingsByItemOwnerId

        final NotFoundException userNotFoundExceptionByGetAll = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingsByItemOwnerId(99L, "ALL", 0, 20)
        );

        assertEquals("Такого пользователя в базе нет", userNotFoundExceptionByGetAll.getMessage());

        List<BookingDto> noBookingsForUser = bookingService.getAllBookingsByItemOwnerId(3L, "ALL", 0, 20);

        assertEquals(0, noBookingsForUser.size());

        List<BookingDto> bookingsByUser1 = bookingService.getAllBookingsByItemOwnerId(1L, "ALL", 0, 20);

        assertEquals(3, bookingsByUser1.size());

    }

}