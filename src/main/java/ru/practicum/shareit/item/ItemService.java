package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsListByOwnerId(Long userId);

    List<ItemDto> searchItemByText(String text);

    CommentDto addComment(Comment comment, Long itemId, Long authorId);

    List<Comment> getCommentsByItemId(Long itemId);

    Boolean itemExistsById(Long itemId);

    ItemDtoForGet getItemByIdForGet(Long itemId);
}
