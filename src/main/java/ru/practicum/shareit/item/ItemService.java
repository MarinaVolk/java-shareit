package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.request.ItemRequestDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsListByOwnerId(Long userId);

    List<Item> getItemsListByOwnerId(Long userId, Integer from, Integer size);

    List<ItemDto> searchItemByText(String text, Integer from, Integer size);

    CommentDto addComment(Comment comment, Long itemId, Long authorId);

    List<Comment> getCommentsByItemId(Long itemId);

    Boolean itemExistsById(Long itemId);

    ItemResponseFullDto getItemByIdForGet(Long itemId);

    //List<ItemRequestDto> getItemsDtoForItemRequestDtoByRequestId(Long requestId);  // rename
}
