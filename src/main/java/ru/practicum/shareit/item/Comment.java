package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * File Name: Comment.java
 * Author: Marina Volkova
 * Date: 2023-07-22,   9:54 PM (UTC+3)
 * Description:
 */
@Data
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "author_id")
    private Long authorId;

    private LocalDateTime created = LocalDateTime.now();

}
