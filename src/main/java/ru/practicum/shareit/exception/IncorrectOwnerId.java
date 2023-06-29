package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: IncorrectOwnerId.java
 * Author: Marina Volkova
 * Date: 2023-06-29,   1:01 PM (UTC+3)
 * Description:
 */
public class IncorrectOwnerId extends RuntimeException {
    public IncorrectOwnerId(String message) {
        super(message);
    }
}
