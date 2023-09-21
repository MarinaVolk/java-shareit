package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * File Name: CommentGatewayDto.java
 * Author: Marina Volkova
 * Date: 2023-09-21,   7:59 PM (UTC+3)
 * Description:
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentGatewayDto {
    private Integer id;
    @NotNull
    @NotEmpty
    private String text;
}
