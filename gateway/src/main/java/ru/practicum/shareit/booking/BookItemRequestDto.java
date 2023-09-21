package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * File Name: BookItemRequestDto.java
 * Author: Marina Volkova
 * Date: 2023-09-20,   11:07 PM (UTC+3)
 * Description:
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}