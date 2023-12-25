package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;


    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersisting() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void verifyRepositoryByPersistingAnEmployee() {
        User user = User.builder().email("12354@mail.ru").name("2tryHard").build();

        Assertions.assertNull(user.getId());
        repository.save(user);
        Assertions.assertNotNull(user.getId());
    }
}
