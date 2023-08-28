package ru.practicum.shareit.item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = " select i from Item i " +
            " where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")

    Page<Item> searchItemForRentByText(String text, Pageable pageable);

    List<Item> findItemsByOwnerId(Long ownerId);

    List<Item> findItemsByRequestId(Long requestId);

    Page<Item> findItemsBy(Pageable pageable);

    Page<Item> findItemsByOwnerId(Long ownerId, Pageable pageable);

}
