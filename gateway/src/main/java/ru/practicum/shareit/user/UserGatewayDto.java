package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * File Name: UserGatewayDto.java
 * Author: Marina Volkova
 * Date: 2023-09-21,   8:05 PM (UTC+3)
 * Description:
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserGatewayDto {
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Email
    private String email;
}
