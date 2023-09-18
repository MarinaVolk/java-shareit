package ru.practicum.shareit.item;/* # parse("File Header.java")*/

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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * File Name: ItemRepositoryTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   9:11 PM (UTC+3)
 * Description:
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final TestEntityManager em;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User savedOwner;
    private User savedRequestor;
    private Item savedItem;
    private ItemRequest savedItemRequest;

    @BeforeEach
        // заполняем репозиторий данными
    void beforeEach() {
        User owner = new User();
        owner.setEmail("user@email.ru");
        owner.setName("user");
        savedOwner = userRepository.save(owner);

        User requestor = new User();
        requestor.setEmail("user23@email.ru");
        requestor.setName("user23");
        savedRequestor = userRepository.save(requestor);

        Item item = new Item();
        item.setName("item1");
        item.setDescription("item1description");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        savedItem = itemRepository.save(item);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("item1_Request_description");
        itemRequest.setRequestor(savedRequestor);
        itemRequest.setCreated(LocalDateTime.now().minusMinutes(5));
        savedItemRequest = itemRequestRepository.save(itemRequest);

        savedItem.setRequestId(savedItemRequest.getId());
        savedItem = itemRepository.save(item);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findItemsByOwnerId_ProvideItemsListByOwnerId() {
        List<Item> foundItems = itemRepository.findItemsByOwnerId(savedOwner.getId());

        assertThat(foundItems.size(), is(1));
        assertTrue(foundItems.contains(savedItem));
    }

    @Test
    void findItemsByRequestId_ProvideItemsListByRequestId() {
        List<Item> foundItems = itemRepository.findItemsByRequestId(savedItemRequest.getId());

        assertThat(foundItems.size(), is(1));
        assertTrue(foundItems.contains(savedItem));
    }

    @Test
    void searchItemForRentByText_ProvidePagesByText() {

        Page<Item> items = itemRepository.searchItemForRentByText("item1",
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(items.getTotalPages(), equalTo(1));

        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo(savedItem.getName())),
                hasProperty("description", equalTo(savedItem.getDescription()))
        )));
    }
}

