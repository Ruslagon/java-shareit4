package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class RequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository repository;


    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersisting() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();
        em.persist(user);

        Request request = Request.builder().created(LocalDateTime.now()).description("хочу лампу")
                .user(user).build();
        Assertions.assertNull(request.getId());
        em.persist(request);
        Assertions.assertNotNull(request.getId());
    }

    @Test
    void verifyRepositoryByPersisting() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        Request request = Request.builder().created(LocalDateTime.now()).description("хочу лампу")
                .user(user).build();

        Assertions.assertNull(request.getId());
        repository.save(request);
        Assertions.assertNotNull(request.getId());
    }

    @Test
    void verifyRepositoryFindByUserId() {
        em.clear();

        User user1 = User.builder().email("12345@mail.ru").name("newMan").build();
        User user2 = User.builder().email("123452@mail.ru").name("newMan2").build();

        Assertions.assertNull(user1.getId());
        em.persist(user1);
        Assertions.assertNotNull(user1.getId());
        Assertions.assertNull(user2.getId());
        em.persist(user2);
        Assertions.assertNotNull(user2.getId());

        Request request = Request.builder().created(LocalDateTime.now()).description("хочу лампу")
                .user(user1).build();
        em.persist(request);

        Request request2 = Request.builder().created(LocalDateTime.now()).description("хочу лампу другу")
                .user(user2).build();
        Request request3 = Request.builder().created(LocalDateTime.now()).description("хочу лампу другу2")
                .user(user2).build();
        em.persist(request2);
        em.persist(request3);

        PageRequest pageRequest = PageRequest.of(0, 50);
        var list = repository.findAllByUserIdOrderByCreatedDesc(user1.getId(), pageRequest).getContent();
        assertThat(list, hasSize(1));

        list = repository.findAllByUserIdOrderByCreatedDesc(user2.getId(), pageRequest).getContent();
        assertThat(list, hasSize(2));
    }

    @Test
    void verifyRepositoryFindAllNotByUser() {
        em.clear();

        User user1 = User.builder().email("12345@mail.ru").name("newMan").build();
        User user2 = User.builder().email("123452@mail.ru").name("newMan2").build();

        Assertions.assertNull(user1.getId());
        em.persist(user1);
        Assertions.assertNotNull(user1.getId());
        Assertions.assertNull(user2.getId());
        em.persist(user2);
        Assertions.assertNotNull(user2.getId());

        Request request = Request.builder().created(LocalDateTime.now()).description("хочу лампу")
                .user(user1).build();
        em.persist(request);

        Request request2 = Request.builder().created(LocalDateTime.now()).description("хочу лампу другу")
                .user(user2).build();
        Request request3 = Request.builder().created(LocalDateTime.now()).description("хочу лампу другу2")
                .user(user2).build();
        em.persist(request2);
        em.persist(request3);

        PageRequest pageRequest = PageRequest.of(0, 50);
        var list = repository.findAllByUserIdNotOrderByCreatedDesc(user1.getId(), pageRequest).getContent();
        assertThat(list, hasSize(2));

        list = repository.findAllByUserIdNotOrderByCreatedDesc(user2.getId(), pageRequest).getContent();
        assertThat(list, hasSize(1));
    }
}
