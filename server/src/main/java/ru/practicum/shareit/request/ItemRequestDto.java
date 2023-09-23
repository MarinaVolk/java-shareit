package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Sprint add-ru.practicum.shareit.item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;

    private String description;

    private Long requestorId;

    private LocalDateTime created;

    private List<ItemDto> items;
}
