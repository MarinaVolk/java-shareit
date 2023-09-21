package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: ItemValidationException.java
 * Author: Marina Volkova
 * Date: 2023-06-29,   11:41 PM (UTC+3)
 * Description:
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
