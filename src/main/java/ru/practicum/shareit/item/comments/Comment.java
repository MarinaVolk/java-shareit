package ru.practicum.shareit.item.comments;/* # parse("File Header.java")*/

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

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

    @ManyToOne
    private Item item;

    @ManyToOne
    private User author;

    private LocalDateTime created = LocalDateTime.now();

}
