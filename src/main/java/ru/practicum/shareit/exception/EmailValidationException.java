package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: EmailValidationException.java
 * Author: Marina Volkova
 * Date: 2023-06-29,   9:18 PM (UTC+3)
 * Description:
 */
public class EmailValidationException extends RuntimeException {
    public EmailValidationException(String message) {
        super(message);
    }
}
