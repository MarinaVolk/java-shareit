package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: UserEmailAlreadyUsedException.java
 * Author: Marina Volkova
 * Date: 2023-06-29,   8:31 PM (UTC+3)
 * Description:
 */
public class UserEmailAlreadyUsedException extends RuntimeException {
    public UserEmailAlreadyUsedException(String message) {
        super(message);
    }
}
