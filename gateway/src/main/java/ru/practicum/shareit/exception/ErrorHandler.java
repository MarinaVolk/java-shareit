package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * File Name: ErrorHandler.java
 * Author: Marina Volkova
 * Date: 2023-09-22,   1:19 PM (UTC+3)
 * Description:
 */

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse illegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

}
