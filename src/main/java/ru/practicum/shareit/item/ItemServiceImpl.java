package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.IncorrectOwnerId;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Name: ItemServiceImpl.java
 * Author: Marina Volkova
 * Date: 2023-06-28,   10:02 PM (UTC+3)
 * Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemValidator validator;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.fromDto(itemDto);

        validator.isValid(item);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Такого пользователя в базе нет.");
        }
        item.setOwner(userRepository.getReferenceById(userId));
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Item updateItem = ItemMapper.fromDto(itemDto);
        Item oldItem = itemRepository.getReferenceById(itemId);

        if (!oldItem.getOwner().getId().equals(userId)) {
            log.error("ItemService: вещь с id={} не принадлежит пользователю с id={}", itemId, userId);
            throw new IncorrectOwnerId("Нельзя редактировать не принадлежащую пользователю вещь.");
        }

        log.info("ItemService: вещь с id={} обновлена.", itemId);
        Item item = itemUpdate(updateItem, oldItem);
        item.setId(itemId);
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }


    @Override
    public ItemDto getItemById(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Вещь не указана.");
        }
        if (!itemRepository.existsById(itemId)) {
            log.error("ItemService: Вещи с id={} в базе нет.", itemId);
            throw new NotFoundException("Такой вещи в базе нет.");
        }
        Item item = itemRepository.getReferenceById(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemResponseFullDto getItemByIdForGet(Long itemId) {
        ItemDto itemDto = getItemById(itemId);
        ItemResponseFullDto itemDtoForGet = ItemMapper.toDtoForGet(itemDto);

        List<Comment> comments = getCommentsByItemId(itemId);
        List<CommentDto> commentDtos = new ArrayList<>();

        for (Comment comment : comments) {
            Long authorId = comment.getAuthor().getId();
            User author = userRepository.getReferenceById(authorId);
            String authorName = author.getName();
            CommentDto commentDto = CommentMapper.toDto(comment);
            commentDto.setAuthorName(authorName);
            commentDtos.add(commentDto);
        }
        itemDtoForGet.setComments(commentDtos);

        return itemDtoForGet;
    }


    @Override
    public List<ItemDto> getItemsListByOwnerId(Long userId) {
        log.info("ItemService: запрос для получения списка вещей владельца с id={} ", userId);
        List<Item> itemsOfOwner = itemRepository.findItemsByOwnerId(userId);
        return itemsOfOwner.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsListByOwnerId(Long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Item> items = itemRepository.findItemsByOwnerId(userId, pageable);

        log.info("ItemService: запрос для получения списка вещей владельца с id={} ", userId);
        return items.getContent();
    }

    @Override
    public List<ItemDto> searchItemByText(String text, Integer from, Integer size) {
        if (!StringUtils.hasText(text)) {
            log.info("ItemService: текст для поиска пустой, список пуст.");
            return new ArrayList<>();
        } else {
            log.info("ItemService: запрос для поиска вещей содержащих текст \"{}\"", text);
            Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
            return itemRepository.searchItemForRentByText(text, pageable).stream()
                    .filter(item -> item.getAvailable().equals(true))
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(Comment comment, Long itemId, Long authorId) {
        if (!StringUtils.hasText(comment.getText())) {
            throw new ValidationException("Текст комментария пустой.");
        }

        comment.setItem(itemRepository.getReferenceById(itemId));
        comment.setAuthor(userRepository.getReferenceById(authorId));
        comment = commentRepository.save(comment);

        User author = userRepository.getReferenceById(authorId);
        String authorName = author.getName();

        CommentDto commentDto = CommentMapper.toDto(comment);
        commentDto.setAuthorName(authorName);

        return commentDto;
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findCommentsByItemId(itemId);
    }

    @Override
    public Boolean itemExistsById(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    private Item itemUpdate(Item updateItem, Item oldItem) {
        Item item = new Item();

        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        } else {
            item.setName(oldItem.getName());
        }

        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        } else {
            item.setDescription(oldItem.getDescription());
        }

        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        } else {
            item.setAvailable(oldItem.getAvailable());
        }

        if (updateItem.getOwner() != null && updateItem.getOwner().getId() != null) {
            item.setOwner(updateItem.getOwner());
        } else {
            item.setOwner(oldItem.getOwner());
        }
        return item;
    }

    @Override
    public List<ItemDto> getItemsForItemRequestDtoByRequestId(Long requestId) {

        return itemRepository.findItemsByRequestId(requestId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

    }

}
