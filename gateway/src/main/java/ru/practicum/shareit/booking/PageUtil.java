package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import ru.practicum.shareit.exception.ValidationException;

/**
 * File Name: PageUtil.java
 * Author: Marina Volkova
 * Date: 2023-09-22,   4:55 PM (UTC+3)
 * Description:
 */

public class PageUtil {

    public static void checkPageParameters(Integer from, Integer size) {
        if (size < 1 || from < 0) {
            throw new ValidationException("Некорректный параметр size или from.");
        }
    }
}
