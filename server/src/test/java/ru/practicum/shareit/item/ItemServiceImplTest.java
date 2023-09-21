package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.IncorrectOwnerId;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplTest {

    private final ItemService itemService;

    private final UserService userService;

    private static ItemDto itemDto1;

    private static ItemDto itemDto2;

    private static UserDto userDto1;

    private static CommentDto commentDto;

    @BeforeAll
    static void beforeAll() {

        itemDto1 = new ItemDto();
        itemDto1.setAvailable(true);
        itemDto1.setDescription("item1Description");
        itemDto1.setName("item1");

        itemDto2 = new ItemDto();
        itemDto2.setAvailable(true);
        itemDto2.setDescription("item2Description");
        itemDto2.setName("item2");

        userDto1 = new UserDto();
        userDto1.setEmail("user1@email");
        userDto1.setName("user1");

    }

    @Test
    void updateItemShouldUpdateItem() {
        userService.addUser(userDto1);

        itemService.addItem(1L, itemDto1);

        ItemDto itemDtoToUpdateName = new ItemDto();
        itemDtoToUpdateName.setName("updateItem1");

        final IncorrectOwnerId incorrectItemOwnerId = assertThrows(
                IncorrectOwnerId.class,
                () -> itemService.updateItem(99L, itemDtoToUpdateName, 1L)
        );

        assertEquals("Нельзя редактировать не принадлежащую пользователю вещь.", incorrectItemOwnerId.getMessage());

        ItemDto updatedItem = itemService.updateItem(1L, itemDtoToUpdateName, 1L);

        assertEquals("updateItem1", updatedItem.getName());

        ItemDto itemDtoToUpdateDescription = new ItemDto();
        itemDtoToUpdateDescription.setDescription("updateItem1Description");

        updatedItem = itemService.updateItem(1L, itemDtoToUpdateDescription, 1L);

        assertEquals("updateItem1Description", updatedItem.getDescription());

        ItemDto itemDtoToUpdateAvailable = new ItemDto();
        itemDtoToUpdateAvailable.setAvailable(false);

        updatedItem = itemService.updateItem(1L, itemDtoToUpdateAvailable, 1L);

        assertFalse(updatedItem.getAvailable());

        ItemDto itemDtoToUpdateOwnerId = new ItemDto();
        itemDtoToUpdateOwnerId.setOwnerId(2L);

        UserDto userDto2 = new UserDto();
        userDto2.setName("user2");
        userDto2.setEmail("user2@email");

        userService.addUser(userDto2);

        updatedItem = itemService.updateItem(1L, itemDtoToUpdateOwnerId, 1L);

        assertEquals(2, updatedItem.getOwnerId());
    }

    @Test
    void getItemByIdShouldProvideItemById() {
        userService.addUser(userDto1);

        itemService.addItem(1L, itemDto1);

        final NotFoundException itemNotFoundException = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(99L)
        );

        assertEquals("Такой вещи в базе нет.", itemNotFoundException.getMessage());

        ItemDto result = itemService.getItemById(1L);

        assertEquals(1, result.getId());
    }

    @Test
    void searchItemByTextShouldProvideItemByText() {
        userService.addUser(userDto1);

        itemService.addItem(1L, itemDto1);
        itemService.addItem(1L, itemDto2);

        List<ItemDto> emptyResultList = itemService.searchItemByText("", 0, 20);

        assertEquals(0, emptyResultList.size());

        emptyResultList = itemService.searchItemByText(null, 0, 20);

        assertEquals(0, emptyResultList.size());

        List<ItemDto> items = itemService.searchItemByText("1", 0, 20);

        assertEquals(1, items.size());

        items = itemService.searchItemByText("2", 0, 20);

        assertEquals(2, items.get(0).getId());

        ItemDto itemDtoToUpdateStatusItem1 = new ItemDto();
        itemDtoToUpdateStatusItem1.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(1L, itemDtoToUpdateStatusItem1, 1L);

        assertFalse(updatedItem.getAvailable());

        items = itemService.searchItemByText("1", 0, 20);

        assertEquals(0, items.size());
    }

    @Test
    void addCommentShouldAddComment() {
        userService.addUser(userDto1);
        itemService.addItem(1L, itemDto1);

        Comment commentWithEmptyText = new Comment();
        commentWithEmptyText.setText("");

        Comment commentWithNullText = new Comment();

        final ValidationException e1 = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(commentWithEmptyText, 1L, 1L)
        );

        assertEquals("Текст комментария пустой.", e1.getMessage());

        final ValidationException e2 = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(commentWithNullText, 1L, 1L)
        );

        assertEquals("Текст комментария пустой.", e2.getMessage());

        Comment comment = new Comment();
        comment.setText("textComment1");

        commentDto = itemService.addComment(comment, 1L, 1L);
        assertEquals(1, comment.getId());
    }
}