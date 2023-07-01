package ru.practicum.shareit.user;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private Long id;
    private String name;
    @NotNull
    private String email;
}
