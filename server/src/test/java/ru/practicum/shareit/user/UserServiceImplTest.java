package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;

import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {

    private final UserService userService;

    private static UserDto userDto1;

    private static UserDto userDto2;

    @BeforeAll
    static void setUp() {
        userDto1 = new UserDto();
        userDto1.setEmail("user1@email");
        userDto1.setName("user1");

        userDto2 = new UserDto();
        userDto2.setEmail("user2@email");
        userDto2.setName("user2");
    }

    @Test
    void updateUserShouldUpdateUser() {
        UserDto userDto1Update = new UserDto();
        userDto1Update.setEmail("user1update@email");
        userDto1Update.setName("user1");

        UserDto userDto_1 = userService.addUser(userDto1);
        Long id = userDto_1.getId();

        UserDto updatedUserDto = userService.updateUser(userDto1Update, id);

        assertEquals("user1update@email", updatedUserDto.getEmail());
    }

    @Test
    void getUserByIdShouldProvideUserById() {
        UserDto userDto_2 = userService.addUser(userDto1);
        Long id = userDto_2.getId();

        UserDto obtainedUserDto = userService.getUserById(id);

        assertEquals(1, obtainedUserDto.getId());

        final NotFoundException userNotFoundException = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(11L)
        );
        assertEquals("Пользователя с id=11 в базе нет.", userNotFoundException.getMessage());
    }

    @Test
    void getAllShouldProvideAllUsers() {
        userService.addUser(userDto1);
        userService.addUser(userDto2);

        Collection<UserDto> users = userService.getAll();

        assertEquals(2, users.size());
    }

    @Test
    void deleteUserByIdShouldDeleteUserById() {
        userService.addUser(userDto1);
        userService.addUser(userDto2);

        Collection<UserDto> users = userService.getAll();

        assertEquals(2, users.size());

        userService.deleteUserById(2L);

        users = userService.getAll();

        assertEquals(1, users.size());
    }

    @Test
    void userExistsByIdShouldVerifyThatUserExists() {
        userService.addUser(userDto1);

        assertTrue(userService.userExistsById(1L));
    }
}