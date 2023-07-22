package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.Data;

import java.time.LocalDateTime;

/**
 * File Name: CommentDto.java
 * Author: Marina Volkova
 * Date: 2023-07-22,   9:56 PM (UTC+3)
 * Description:
 */
@Data
public class CommentDto {
    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;

    private Long authorId;
}
