package ru.practicum.shareit.exception;/* # parse("File Header.java")*/

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * File Name: ErrorHandler.java
 * Author: Marina Volkova
 * Date: 2023-06-29,   9:54 PM (UTC+3)
 * Description:
 */
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse emailValidationExceptionHandler(final UserEmailAlreadyUsedException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse emailValidationExceptionHandler2(final EmailValidationException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse requestValidationExceptionHandler(final IncorrectOwnerId e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse ItemValidationExceptionHandler(final ValidationException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(final NotFoundException e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherExceptionHandler(final Exception e) {
        return new ErrorResponse("Error:" + e.getMessage());
    }

}
