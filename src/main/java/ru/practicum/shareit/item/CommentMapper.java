package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * File Name: CommentMapper.java
 * Author: Marina Volkova
 * Date: 2023-07-22,   9:58 PM (UTC+3)
 * Description:
 */
@Component
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {

        CommentDto commentDto = new CommentDto();

        commentDto.setCreated(comment.getCreated());
        commentDto.setText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());

        Optional.ofNullable(comment.getId()).ifPresent(commentDto::setId);

        return commentDto;
    }
}
