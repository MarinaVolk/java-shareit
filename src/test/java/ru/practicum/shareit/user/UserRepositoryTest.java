package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.TypedQuery;
/**
 * File Name: UserRepositoryTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   8:31 PM (UTC+3)
 * Description:
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {
    private final TestEntityManager em;
    private final UserRepository userRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyRepository() {
        User user = new User();
        user.setEmail("user1@email.ru");
        user.setName("user1");

        Assertions.assertNull(user.getId());
        User newUser = userRepository.save(user);
        Assertions.assertNotNull(newUser.getId());

        TypedQuery<User> query = em.getEntityManager().createQuery("select u from User u where u.id = :id", User.class);
        User foundUser = query.setParameter("id", newUser.getId()).getSingleResult();

        Assertions.assertEquals(foundUser.getId(), newUser.getId());
    }
}