package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @NotNull
    private Long ownerId;
    private Long requestId;
}
