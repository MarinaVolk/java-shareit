package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * File Name: ItemGatewayDto.java
 * Author: Marina Volkova
 * Date: 2023-09-21,   7:51 PM (UTC+3)
 * Description:
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemGatewayDto {
    private long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
