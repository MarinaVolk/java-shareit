package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
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

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyRepository() {
        Item item = new Item();
        item.setName("item1");
        item.setDescription("item1description");
        item.setAvailable(true);

        Assertions.assertNull(item.getId());
        Item newItem = itemRepository.save(item);
        Assertions.assertNotNull(newItem.getId());

        TypedQuery<Item> query = em.getEntityManager().createQuery(
                "select it from Item it where it.id = :id",
                Item.class);
        Item foundItem = query.setParameter("id", newItem.getId()).getSingleResult();

        Assertions.assertEquals(foundItem.getId(), newItem.getId());
    }

    @Test
    void searchItemForRentByTextPositive() {
        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("item1description");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("item2description");
        item2.setAvailable(true);

        itemRepository.save(item1);
        itemRepository.save(item2);

        Page<Item> items = itemRepository.searchItemForRentByText("search",
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(items.getTotalPages(), equalTo(0));

        items = itemRepository.searchItemForRentByText("item",
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo(item1.getName())),
                hasProperty("description", equalTo(item1.getDescription()))
        )));
        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo(item2.getName())),
                hasProperty("description", equalTo(item2.getDescription()))
        )));
    }

}

