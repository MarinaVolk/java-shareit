package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * File Name: BookingDtoJsonTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   10:05 PM (UTC+3)
 * Description:
 */
@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void bookingDtoTest() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(5).truncatedTo(ChronoUnit.SECONDS);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(localDateTime);
        bookingDto.setEnd(localDateTime.plusMinutes(15));

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(localDateTime.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(
                localDateTime.plusMinutes(15).format(formatter));
    }
}
