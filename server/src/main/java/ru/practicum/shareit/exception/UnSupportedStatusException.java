package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: UnSupportedStatusException.java
 * Author: Marina Volkova
 * Date: 2023-07-24,   7:10 PM (UTC+3)
 * Description:
 */
public class UnSupportedStatusException extends RuntimeException {
    public UnSupportedStatusException(String message) {
        super(message);
    }
}
