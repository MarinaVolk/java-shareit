package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: ValidationException.java
 * Author: Marina Volkova
 * Date: 2023-09-22,   4:56 PM (UTC+3)
 * Description:
 */

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}