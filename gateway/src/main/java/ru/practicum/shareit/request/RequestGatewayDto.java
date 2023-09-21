package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * File Name: RequestGatewayDto.java
 * Author: Marina Volkova
 * Date: 2023-09-21,   8:00 PM (UTC+3)
 * Description:
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestGatewayDto {
    private Integer id;
    @NotNull
    @NotEmpty
    private String description;
}