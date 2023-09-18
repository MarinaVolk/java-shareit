package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ValidationException;

/**
 * File Name: PageUtil.java
 * Author: Marina Volkova
 * Date: 2023-09-16,   10:32 PM (UTC+3)
 * Description:
 */
public class PageUtil {

    public static Pageable createPage(Integer from, Integer size, String sortBy) {
        checkPageParameters(from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(sortBy).descending());
        return pageable;
    }

    private static void checkPageParameters(Integer from, Integer size) {
        if (size < 1 || from < 0) {
            throw new ValidationException("Некорректный параметр size или from.");
        }
    }
}
