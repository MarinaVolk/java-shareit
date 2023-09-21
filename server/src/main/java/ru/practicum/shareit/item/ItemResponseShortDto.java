package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * File Name: ItemResponseShortDtoDto.java
 * Author: Marina Volkova
 * Date: 2023-07-21,   9:19 PM (UTC+3)
 * Description:
 */
@Data
public class ItemResponseShortDto {

    private final Long id;
    private String name;

    @JsonCreator
    public ItemResponseShortDto(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

}
