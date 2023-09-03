package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemResponseShortDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comments.CommentDto;
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
class ItemRequestServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

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

    private static ItemRequest createItemRequest(String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestorId(1L);
        itemRequest.setCreated(LocalDateTime.now().plusDays(1));
        return itemRequest;
    }

    private static CommentDto createComment(String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        return commentDto;
    }

    @Test
    void addItemRequestShouldAddItemRequest() {

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

        ItemRequest itemRequest1 = createItemRequest("descriptionItemRequest1");

        //Тестируем добавление запроса
        final NotFoundException userNotFoundException = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.addItemRequest(itemRequest1, 99L)
        );

        assertEquals("Такого пользователя в базе нет.", userNotFoundException.getMessage());

        ItemRequestDto itemRequestDto1 = itemRequestService.addItemRequest(itemRequest1, 3L);

        assertEquals(1, itemRequestDto1.getId());

        //Тестируем получение запроса по id
        final NotFoundException userNotFoundExceptionByGetId = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(99L, 1L)
        );

        assertEquals("Такого пользователя в базе нет.", userNotFoundExceptionByGetId.getMessage());

        final NotFoundException itemRequestNotFoundException = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(2L, 99L)
        );

        assertEquals("Запроса в базе нет.", itemRequestNotFoundException.getMessage());

        ItemRequestDto itemRequestDtoById = itemRequestService.getItemRequestById(2L, 1L);

        assertEquals(1, itemRequestDtoById.getId());

        //Тестируем findAllRequestsByRequestorId
        final NotFoundException userNotFoundExceptionByFindAllByRequestorId = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findItemRequestsByRequestorId(99L)
        );

        assertEquals("Такого пользователя в базе нет.", userNotFoundExceptionByFindAllByRequestorId.getMessage());

        ItemRequest itemRequest2 = createItemRequest("descriptionItemRequest2");
        ItemRequestDto itemRequestDto2 = itemRequestService.addItemRequest(itemRequest2, 3L);
        List<ItemRequestDto> itemRequestsByRequestorId = itemRequestService.findItemRequestsByRequestorId(3L);

        assertEquals(2, itemRequestsByRequestorId.size());

        //Тестируем findAllRequestsByPages
        final NotFoundException userNotFoundExceptionByFindAll = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findAllRequestsByPages(99L, 0, 20)
        );

        assertEquals("Такого пользователя в базе нет.", userNotFoundExceptionByFindAll.getMessage());

        final ValidationException invalidParametersException1 = assertThrows(
                ValidationException.class,
                () -> itemRequestService.findAllRequestsByPages(1L, -1, 20)
        );

        assertEquals("Параметр size или from некорректный", invalidParametersException1.getMessage());

        final ValidationException invalidParametersException2 = assertThrows(
                ValidationException.class,
                () -> itemRequestService.findAllRequestsByPages(1L, 0, 0)
        );

        assertEquals("Параметр size или from некорректный", invalidParametersException2.getMessage());

        List<ItemRequestDto> itemRequestsByFindAll = itemRequestService.findAllRequestsByPages(1L, 0, 20);

        assertEquals(2, itemRequestsByFindAll.size());

        ItemDto itemDto4 = createItemDto("item4Description", "item4", true);
        itemDto4.setRequestId(1L);

        itemDto4 = itemService.addItem(1L, itemDto4);
        ItemRequestDto itemRequestDtoForItem4 = itemRequestService.getItemRequestById(1L, 1L);

        assertEquals(1, itemRequestDtoForItem4.getItems().size());
    }
}