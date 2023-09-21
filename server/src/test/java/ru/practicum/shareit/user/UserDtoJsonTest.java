package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * File Name: UserDtoJsonTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   8:13 PM (UTC+3)
 * Description:
 */
@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userDtoTest() throws IOException {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user1@email");
        userDto.setName("user1");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user1@email");
    }
}
