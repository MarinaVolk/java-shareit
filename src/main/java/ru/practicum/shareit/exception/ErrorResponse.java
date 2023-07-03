package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

/**
 * File Name: ErrorResponse.java
 * Author: Marina Volkova
 * Date: 2023-06-29,   9:54 PM (UTC+3)
 * Description:
 */
public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
