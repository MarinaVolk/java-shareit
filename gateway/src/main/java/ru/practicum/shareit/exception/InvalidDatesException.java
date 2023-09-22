package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: InvalidDatesException.java
 * Author: Marina Volkova
 * Date: 2023-09-22,   4:50 PM (UTC+3)
 * Description:
 */

public class InvalidDatesException extends RuntimeException {
    public InvalidDatesException(String message) {
        super(message);
    }
}
