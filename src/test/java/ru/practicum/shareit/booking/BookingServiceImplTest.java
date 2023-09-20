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

    @Test
    void addBookingExceptionWhenWrongBookerId() {

        // GIVEN: созданы объекты userDto, itemDto и bookingDto
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        UserDto userDto1 = createUserDto("user1@email", "user1");
        userDto1 = userService.addUser(userDto1);
        itemDto1 = itemService.addItem(1L, itemDto1);

        BookingDto booking = createBookingDto(BookingStatus.APPROVED, 1L, 1L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(3));

        //WHEN: при попытке добавления букинга с несуществующим bookerId получаем ошибку NotFoundException
        final NotFoundException userNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(booking, 99L)
        );
        // THEN: NotFoundException
        assertEquals("Пользователя с id=99 в базе нет.", userNotFoundException.getMessage());
    }

    @Test
    void addBookingExceptionWhenWrongItemId() {

        // GIVEN: созданы объекты userDto, itemDto и bookingDto
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        UserDto userDto1 = createUserDto("user1@email", "user1");
        userDto1 = userService.addUser(userDto1);
        itemDto1 = itemService.addItem(1L, itemDto1);

        BookingDto bookingWithWrongItemId = createBookingDto(BookingStatus.APPROVED, 99L, 1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        //WHEN: при попытке добавления букинга с несуществующим itemId получаем ошибку NotFoundException
        final NotFoundException itemNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingWithWrongItemId, 1L)
        );
        // THEN: NotFoundException
        assertEquals("Такой вещи в базе нет.", itemNotFoundException.getMessage());
    }

    @Test
    void addBookingExceptionWhenFalseAvailableStatus() {

        // GIVEN: созданы объекты userDto, itemDto и bookingDto. У itemDto2 статус Availability = false
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        ItemDto itemDto2 = createItemDto("item2Description", "item2", false);
        UserDto userDto1 = createUserDto("user1@email", "user1");
        userDto1 = userService.addUser(userDto1);
        UserDto userDto2 = createUserDto("user2@email", "user2");
        userDto2 = userService.addUser(userDto2);
        itemDto1 = itemService.addItem(1L, itemDto1);
        itemDto2 = itemService.addItem(2L, itemDto2);

        BookingDto bookingForFalseAvailableStatus = createBookingDto(BookingStatus.APPROVED, 2L, 2L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        //WHEN: при попытке добавления букинга со статусом Availability = False получаем ошибку ValidationException
        final ValidationException incorrectItemStatusForBookingException = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookingForFalseAvailableStatus, 2L)
        );
        // THEN: сообщение, что вещь недоступна для бронирования.
        assertEquals("Вещь недоступна для бронирования.", incorrectItemStatusForBookingException.getMessage());
    }


    @Test
    void addBookingExceptionWhenOwnerIsBooker() {
        // GIVEN: созданы объекты userDto, itemDto и bookingDto
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        UserDto userDto1 = createUserDto("user1@email", "user1");
        userDto1 = userService.addUser(userDto1);
        itemDto1 = itemService.addItem(1L, itemDto1);

        BookingDto bookingForItemFromOwner = createBookingDto(BookingStatus.APPROVED, 1L, 1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));

        // WHEN: если владелец указан в качестве букера, то получаем ошибку
        final NotFoundException bookingNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingForItemFromOwner, 1L)
        );
        // THEN: сообщение, что владелец не может бронировать свою вещь
        assertEquals("Владелец не может бронировать свою вещь.", bookingNotFoundException.getMessage());
    }


    @Test
    void getBookingsExceptionWhenWrongBookerId() throws InterruptedException {
        // GIVEN: созданы объекты userDto, itemDto и bookingDto
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        UserDto userDto1 = createUserDto("user1@email", "user1");
        userDto1 = userService.addUser(userDto1);
        itemDto1 = itemService.addItem(1L, itemDto1);

        //WHEN: попытка получить bookings по неверному bookerId
        final NotFoundException userNotFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingsByBookerIdDesc(99L, "PAST", 0, 20)
        );
        //THEN: получаем сообщение об ошибке
        assertEquals("Такого пользователя в базе нет.", userNotFoundException.getMessage());
    }


    @Test
    void getBookingsShouldProvideBookingsUponRequest() throws InterruptedException {
        // GIVEN: созданы объекты userDto, itemDto и bookingDto
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

        // GIVEN: созданы три букинга bookingPast, bookingCurrent, bookingFuture
        BookingDto bookingPast = createBookingDto(BookingStatus.APPROVED, 1L, 3L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(3));

        BookingDto bookingCurrent = createBookingDto(BookingStatus.APPROVED, 1L, 3L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusDays(1));

        BookingDto bookingFuture = createBookingDto(BookingStatus.APPROVED, 2L, 3L,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4));

        // WHEN: пока букинги не добавлены через bookingService.addBooking, то getAllBookingsByBookerIdDesc()
        // возвращает пустой список
        List<BookingDto> bookingsEmpty = bookingService.getAllBookingsByBookerIdDesc(3L, "ALL", 0, 20);

        // THEN: пустой список
        assertEquals(0, bookingsEmpty.size());


        // WHEN: букинги добавлены через bookingService.addBooking()
        BookingDto bookingDtoPast = bookingService.addBooking(bookingPast, 3L);
        BookingDto bookingDtoCurrent = bookingService.addBooking(bookingCurrent, 3L);
        BookingDto bookingDtoFuture = bookingService.addBooking(bookingFuture, 3L);

        Thread.sleep(4000);

        // THEN: при запросе букингов со статусом "CURRENT", получаем их
        List<BookingDto> bookingsCurrent = bookingService.getAllBookingsByBookerIdDesc(3L, "CURRENT", 0, 20);
        assertEquals(1, bookingsCurrent.size());

        // THEN: при запросе букингов со статусом "PAST", получаем их
        List<BookingDto> bookingsPast = bookingService.getAllBookingsByBookerIdDesc(3L, "PAST", 0, 20);
        assertEquals(1, bookingsPast.size());

        // THEN: при запросе букингов со статусом "FUTURE", получаем их
        List<BookingDto> bookingsFuture = bookingService.getAllBookingsByBookerIdDesc(3L, "FUTURE", 0, 20);
        assertEquals(1, bookingsFuture.size());

        // THEN: при запросе букингов со статусом "REJECTED", получаем их, если есть
        List<BookingDto> bookingsRejected = bookingService.getAllBookingsByBookerIdDesc(3L, "REJECTED", 0, 20);
        assertEquals(0, bookingsRejected.size());

        // THEN: при запросе букингов со статусом "ALL", получаем их
        List<BookingDto> bookingsAll = bookingService.getAllBookingsByBookerIdDesc(3L, "ALL", 0, 20);
        assertEquals(3, bookingsAll.size());
    }

    @Test
    void getBookingsExceptionWhenUnknownStatus() throws InterruptedException {
        // GIVEN: созданы объекты userDto, itemDto и bookingDto
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        UserDto userDto1 = createUserDto("user1@email", "user1");
        userDto1 = userService.addUser(userDto1);
        itemDto1 = itemService.addItem(1L, itemDto1);

        BookingDto bookingPast = createBookingDto(BookingStatus.APPROVED, 1L, 1L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(3));

        // WHEN: при запросе букингов с несуществующим статусом, получаем ошибку UnSupportedStatusException
        final UnSupportedStatusException unSupportedStatusException = assertThrows(
                UnSupportedStatusException.class,
                () -> bookingService.getAllBookingsByBookerIdDesc(1L, "WWW", 0, 20)
        );
        // THEN: Сообщение "Unknown state: WWW"
        assertEquals("Unknown state: WWW", unSupportedStatusException.getMessage());
    }


    @Test
    void getBookingsExceptionWhenIncorrectOwnerId() throws InterruptedException {
        // GIVEN: созданы объекты userDto, itemDto и bookingDto
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        UserDto userDto1 = createUserDto("user1@email", "user1");
        userDto1 = userService.addUser(userDto1);
        itemDto1 = itemService.addItem(1L, itemDto1);

        BookingDto bookingPast = createBookingDto(BookingStatus.APPROVED, 1L, 1L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(3));

        // WHEN: при запросе букингов с несуществующим OwnerId, получаем ошибку NotFoundException
        final NotFoundException userNotFoundExceptionByGetAll = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingsByItemOwnerId(99L, "ALL", 0, 20)
        );
        // THEN: сообщение об ошибке
        assertEquals("Такого пользователя в базе нет", userNotFoundExceptionByGetAll.getMessage());
    }

    @Test
    void getAllBookingsByItemOwnerIdPositiveTest() throws InterruptedException {
        // GIVEN: созданы объекты userDto, itemDto и bookingDto
        ItemDto itemDto1 = createItemDto("item1Description", "item1", true);
        ItemDto itemDto2 = createItemDto("item2Description", "item2", true);
        ItemDto itemDto3 = createItemDto("item3Description", "item3", true);

        UserDto userDto1 = createUserDto("user1@email", "user1");
        UserDto userDto2 = createUserDto("user2@email", "user2");

        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);
        itemDto1 = itemService.addItem(1L, itemDto1);
        itemDto2 = itemService.addItem(1L, itemDto2);
        itemDto3 = itemService.addItem(1L, itemDto3);

        BookingDto bookingPast = createBookingDto(BookingStatus.APPROVED, 1L, 2L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(3));

        BookingDto bookingCurrent = createBookingDto(BookingStatus.APPROVED, 2L, 2L,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusDays(1));

        BookingDto bookingFuture = createBookingDto(BookingStatus.APPROVED, 3L, 2L,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4));

        BookingDto bookingDtoPast = bookingService.addBooking(bookingPast, 2L);
        BookingDto bookingDtoCurrent = bookingService.addBooking(bookingCurrent, 2L);
        BookingDto bookingDtoFuture = bookingService.addBooking(bookingFuture, 2L);

        // WHEN: при запросе букингов по ItemOwnerId выдается соответствующее количество букингов
        List<BookingDto> noBookingsForUser = bookingService.getAllBookingsByItemOwnerId(2L, "ALL", 0, 20);
        // THEN: при отсутствии букингов пуст ой список
        assertEquals(0, noBookingsForUser.size());

        // THEN: при наличии букингов - список букингов
        List<BookingDto> bookingsByUser1 = bookingService.getAllBookingsByItemOwnerId(1L, "ALL", 0, 20);
        assertEquals(3, bookingsByUser1.size());
    }
}