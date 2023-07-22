package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = " select i from Item i " +
            " where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")

    List<Item> searchItemForRentByText(String text);

    List<Item> findItemsByOwnerId(Long ownerId);

    /*Item addItem(Item item);

    Item getItemById(Long itemId);

    Item updateItem(Item item);

    Boolean itemIsContained(Long itemId);

    List<Item> getAll();

    List<Item> getItemListByOwnerId(Long userId);

    List<Item> searchItemByText(String text);*/
}
